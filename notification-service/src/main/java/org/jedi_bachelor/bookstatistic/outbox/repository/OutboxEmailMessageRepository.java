package org.jedi_bachelor.bookstatistic.outbox.repository;

import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxEmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEmailMessageRepository
        extends JpaRepository<OutboxEmailMessage, Long> {
    List<OutboxEmailMessage> findByPublishedFalse();
}
