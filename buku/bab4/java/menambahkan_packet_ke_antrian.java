@Component
public class PacketProducer {
    ...
    @Scheduled(fixedDelay = 30 * 1000, initialDelay = 15 * 1000)
    public void queuePacket() {
        var packets = packetService.findPending();
        packetService.bulkUpdateStatus(packets, PacketStatus.WAITING);
        kafkaService.bulkSendAsync(packets);
        LOG.info("{} packet queued.", packets.size());
    }
}

@Service
public class PacketService {
    ...
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Packet> findPending() {
        return packetRepository.findByBatch_DeliveryDateBeforeAndStatus(LocalDateTime.now(), PacketStatus.CREATED);
    }

    @Transactional
    public List<Packet> bulkUpdateStatus(List<Packet> packets, PacketStatus status) {
        packets.forEach(packet -> packet.setStatus(status));
        return packetRepository.saveAll(packets);
    }
    ....
}

@Service
public class KafkaService {
    ...
    public List<Future<RecordMetadata>> bulkSendAsync(List<Packet> packets) {
        return packets.stream()
                .map(this::sendAsync)
                .collect(Collectors.toList());
    }

    public Future<RecordMetadata> sendAsync(Packet packet) {
        var topic = packet.getDevice().getType().getTopic();
        var record = new ProducerRecord<>(topic, packet.getId().toString(), packet);
        LOG.info("send record with id: {}, into topic: {}.", record.key(), record.topic());
        return producer.send(record);
    }
}

