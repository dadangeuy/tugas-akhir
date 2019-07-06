@Component
public class PacketConsumer {
    ...
    @KafkaListener(topics = {PlatformType.ANDROID_TOPIC, PlatformType.WEB_TOPIC})
    public void consume(ConsumerRecord<String, Packet> record) {
        LOG.info("consume record {}.", record.key());
        var packet = record.value();
        try {
            fcmService.sendPacket(packet.getBatch().getClientDestination(), packet);
        } catch (Exception e) {
            packetService.updateSent(packet, PacketStatus.FAILED, e.toString());
        }
    }
}

@Service
public class FcmService {
    ...
    public Packet sendPacket(Client client, Packet packet) throws FirebaseMessagingException, IOException, SQLException {
        LOG.info("send packet {}.", packet.getId());
        var internalClient = clientService.getInternalClient(client);
        var message = createMessage(packet);
        internalClient.send(message);
        return packetService.updateSent(packet, PacketStatus.SUCCESS, null);
    }

    private Message createMessage(Packet packet) {
        var batch = packet.getBatch();
        var device = packet.getDevice();
        var builder = Message.builder();
        builder.setToken(device.getToken());
        builder.setNotification(new Notification(batch.getTitle(), batch.getBody()));
        if (batch.getImage() != null) builder.putData("image", batch.getImage());
        if (batch.getSound() != null) builder.putData("sound", batch.getSound());
        if (batch.getAction() != null) builder.putData("action", batch.getAction());
        if (batch.getAdditionalData() != null) builder.putData("additional_data", batch.getAdditionalData());
        return builder.build();
    }
}

@Service
public class FcmClientService {
    ...
    @Cacheable(cacheNames = "FirebaseMessaging", key = "#client.id.toString()", sync = true)
    public FirebaseMessaging getInternalClient(Client client) throws IOException, SQLException {
        LOG.info("initialize fcm-client for client {}.", client.getId());
        var certificate = certificateService.findFcmCertificate(client);
        var keyStream = certificate.getKeyStream();
        var googleCredentials = GoogleCredentials.fromStream(keyStream);
        var threadManager = new ConcurrentThreadManager(8);
        var options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setThreadManager(threadManager)
                .build();
        var firebaseApp = FirebaseApp.initializeApp(options, UUID.randomUUID().toString());
        executor.schedule(firebaseApp::delete, 30, TimeUnit.MINUTES);
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

@Service
public class CertificateService {
    ...
    public Certificate findFcmCertificate(Client client) {
        return repository
                .findByClientAndType(client, SenderType.FCM)
                .orElseThrow(() -> FCM_CERTIFICATE_NOT_FOUND);
    }
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

