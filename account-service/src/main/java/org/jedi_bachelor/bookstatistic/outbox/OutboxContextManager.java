package org.jedi_bachelor.bookstatistic.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxAnalyzeMessageRepository;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxNotificationSettingsMessageRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxContextManager {
    private final OutboxNotificationSettingsMessageRepository outboxNotificationSettingsMessageRepository;

    private final OutboxAnalyzeMessageRepository outboxAnalyzeMessageRepository;
}
