package org.jedi_bachelor.bookstatistic.bookservice.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.repository.OutboxAnalyzeRepository;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.repository.OutboxNotificationEntityRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxContentManager {
    private final OutboxNotificationEntityRepository outboxNotificationEntityRepository;

    private final OutboxAnalyzeRepository outboxAnalyzeRepository;
}
