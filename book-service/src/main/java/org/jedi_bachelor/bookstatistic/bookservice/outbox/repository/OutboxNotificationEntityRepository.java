package org.jedi_bachelor.bookstatistic.bookservice.outbox.repository;

import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxNotificationEntityRepository
        extends JpaRepository<OutboxNotificationEntity, UUID> {
}
