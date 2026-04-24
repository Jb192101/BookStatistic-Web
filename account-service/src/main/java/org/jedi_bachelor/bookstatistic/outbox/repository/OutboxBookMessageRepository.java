package org.jedi_bachelor.bookstatistic.outbox.repository;

import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxBookMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxBookMessageRepository
        extends JpaRepository<OutboxBookMessage, Long> {
    List<OutboxBookMessage> findByPublishedFalse();
}
