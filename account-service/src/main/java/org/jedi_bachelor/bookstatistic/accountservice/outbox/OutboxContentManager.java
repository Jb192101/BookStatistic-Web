package org.jedi_bachelor.bookstatistic.accountservice.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.repository.OutboxNotificationSettingsMessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxContentManager {
    private final OutboxNotificationSettingsMessageRepository outboxNotificationSettingsMessageRepository;

    public OutboxNotificationSettingsMessage save(OutboxNotificationSettingsMessage message) {
        return this.outboxNotificationSettingsMessageRepository.save(message);
    }

    public List<OutboxNotificationSettingsMessage> findNotificationSettingsMessageByStatusFalse() {
        return this.outboxNotificationSettingsMessageRepository.findByPublishedFalse();
    }
}
