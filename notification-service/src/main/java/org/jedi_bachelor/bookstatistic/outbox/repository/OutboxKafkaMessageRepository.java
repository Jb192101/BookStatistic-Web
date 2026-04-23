package org.jedi_bachelor.bookstatistic.outbox.repository;

import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxKafkaMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxKafkaMessageRepository
        extends JpaRepository<OutboxKafkaMessage, Long> {
    List<OutboxKafkaMessage> findByPublishedFalse();
}
