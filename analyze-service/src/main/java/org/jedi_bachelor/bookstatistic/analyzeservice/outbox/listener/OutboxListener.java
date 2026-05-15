package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.OutboxContextManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxListener {
    private final KafkaProducer kafkaProducer;

    private final OutboxContextManager outboxContextManager;

    /**
     * Метод отправки сообщения в Kafka
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void prePersistOutboxKafkaMessage() {

    }
}
