package org.jedi_bachelor.bookstatistic.outbox.repository;

import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxNotificationSettingsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxNotificationSettingsMessageRepository
        extends JpaRepository<OutboxNotificationSettingsMessage, Long> {
}
