package org.jedi_bachelor.bookstatistic.analyzeservice.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.repository.OutboxKafkaMessageRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxContextManager {
    private final OutboxKafkaMessageRepository outboxKafkaMessageRepository;
}
