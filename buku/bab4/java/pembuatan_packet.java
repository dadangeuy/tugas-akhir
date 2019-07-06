@Component
public class PacketProducer {
    ...
    @Scheduled(fixedDelay = 30 * 1000)
    public void createPacket() {
        var batches = batchService.findNew();
        batchService.bulkUpdateStart(batches);
        batches.forEach(packetService::bulkCreate);
        batchService.bulkUpdateFinish(batches);
        LOG.info("packet created for {} batches.", batches.size());
    }
    ...
}

@Service
public class BatchService {
    ...
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Batch> findNew() {
        return batchRepository.findByStartedAtIsNullAndAllowedTrue();
    }

    @Transactional
    public List<Batch> bulkUpdateStart(List<Batch> batches) {
        var now = LocalDateTime.now();
        batches.forEach(batch -> batch.setStartedAt(now));
        return batchRepository.saveAll(batches);
    }

    @Transactional
    public List<Batch> bulkUpdateFinish(List<Batch> batches) {
        var now = LocalDateTime.now();
        batches.forEach(batch -> batch.setFinishedAt(now));
        return batchRepository.saveAll(batches);
    }
}

@Service
public class PacketService {
    ...
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Packet> bulkCreate(Batch batch) {
        var devices = deviceRepository.findByBatchIdAndActive(batch.getId().toString());
        var packets = devices.stream()
                .map(device -> createPacket(batch, device))
                .collect(Collectors.toList());
        return packetRepository.saveAll(packets);
    }

    private Packet createPacket(Batch batch, Device device) {
        var packet = new Packet();
        packet.setBatch(batch);
        packet.setDevice(device);
        packet.setStatus(PacketStatus.CREATED);
        return packet;
    }
}

