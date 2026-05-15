package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.repository;

import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity.OutboxKafkaMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxKafkaMessageRepository extends JpaRepository<OutboxKafkaMessage, UUID> {
}
