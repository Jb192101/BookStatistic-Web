package org.jedi_bachelor.bookstatistic.outbox.repository;

import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxNotificationSettingsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxNotificationSettingsMessageRepository
        extends JpaRepository<OutboxNotificationSettingsMessage, Long> {
    List<OutboxNotificationSettingsMessage> findByPublishedFalse();
}
