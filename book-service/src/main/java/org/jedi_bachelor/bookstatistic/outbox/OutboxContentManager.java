package org.jedi_bachelor.bookstatistic.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxNotificationEntityRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxContentManager {
    private final OutboxNotificationEntityRepository outboxNotificationEntityRepository;
}
