@Component
public class PacketConsumer {
    ...
    @KafkaListener(topics = PlatformType.IOS_TOPIC)
    public void consume(ConsumerRecord<String, Packet> record) {
        LOG.info("consume record {}.", record.key());
        var packet = record.value();
        try {
            var client = packet.getBatch().getClientDestination();
            apnService.sendPacket(client, packet);
        } catch (Exception e) {
            packetService.updateSent(packet, PacketStatus.FAILED, e.toString());
        }
    }
}

@Service
public class ApnService {
    ...
    public Packet sendPacket(Client client, Packet packet) throws ExecutionException, InterruptedException, ApnsClientException, IOException, SQLException {
        LOG.info("send packet {}.", packet.getId());
        var internalClient = clientService.getInternalClient(client);
        var notification = createNotification(packet);
        var response = internalClient.sendNotification(notification).get();
        if (!response.isAccepted()) throw new ApnsClientException(response.getRejectionReason());
        return packetService.updateSent(packet, PacketStatus.SUCCESS, null);
    }

    private ApnsPushNotification createNotification(Packet packet) {
        var token = packet.getDevice().getToken();
        var client = packet.getBatch().getClientDestination();
        var certificate = certificateService.findApnCertificate(client);
        var bundleId = certificate.getBundleId();
        var batch = packet.getBatch();
        var payload = createPayload(batch);
        return new SimpleApnsPushNotification(token, bundleId, payload);
    }

    private String createPayload(Batch batch) {
        var builder = new ApnsPayloadBuilder();
        builder.setAlertTitle(batch.getTitle());
        builder.setAlertBody(batch.getBody());
        if (batch.getImage() != null) builder.addCustomProperty("image", batch.getImage());
        if (batch.getSound() != null) builder.addCustomProperty("sound", batch.getSound());
        if (batch.getAction() != null) builder.addCustomProperty("action", batch.getAction());
        if (batch.getAdditionalData() != null) builder.addCustomProperty("additional_data", batch.getAdditionalData());
        return builder.buildWithDefaultMaximumLength();
    }
}

@Service
public class ApnClientService {
    ...
    @Cacheable(cacheNames = "ApnsClient", key = "#client.id.toString()", sync = true)
    public ApnsClient getInternalClient(Client client) throws SQLException, IOException {
        LOG.info("initialize apns-client for client {}.", client.getId());
        var certificate = certificateService.findApnCertificate(client);
        var apnsClient = new ApnsClientBuilder()
                .setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                .setClientCredentials(certificate.getKeyStream(), certificate.getPassword())
                .setConcurrentConnections(8)
                .build();
        executor.schedule(apnsClient::close, 30, TimeUnit.MINUTES);
        return apnsClient;
    }
}

@Service
public class CertificateService {
    ...
    public Certificate findApnCertificate(Client client) {
        return repository
                .findByClientAndType(client, SenderType.APN)
                .orElseThrow(() -> APN_CERTIFICATE_NOT_FOUND);
    }
    ...
}

@Service
public class PacketService {
    ...
    @Transactional
    public Packet updateSent(Packet packet, PacketStatus status, String reason) {
        packet.setStatus(status);
        packet.setReason(reason);
        packet.setSentAt(LocalDateTime.now());
        return packetRepository.save(packet);
    }
    ...
}

