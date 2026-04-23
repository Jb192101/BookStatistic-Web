package org.jedi_bachelor.bookstatistic.outbox.listener;

import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.service.EmailService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxListener {
    private final KafkaProducer kafkaProducer;

    private final EmailService emailService;

    @PrePersist
    public void prePersistOutboxEmailMessage(OutboxEmailMessage message) {
        // Формирование EmailContext

        // Отправка сообщения
    }

    @PrePersist
    public void prePersistOutboxKafkaMessage(OutboxKafkaMessage message) {
        // Формирование KafkaMessageDto

        // Отправка в Kafka
    }
}
