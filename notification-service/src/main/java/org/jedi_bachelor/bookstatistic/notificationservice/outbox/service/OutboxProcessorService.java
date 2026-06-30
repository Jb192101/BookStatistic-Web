package org.jedi_bachelor.bookstatistic.notificationservice.outbox.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.notificationservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.dto.OutboxKafkaDto;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessorService {
    private final KafkaProducer kafkaProducer;

    private final OutboxContentManager outboxContentManager;

    private static final int MAX_RETRIES = 5;

    @Transactional
    public void processOutboxMessage(OutboxKafkaMessage message) {
        try {
            log.info("Processing outbox message: {}", message.getId());

            // Формируем DTO для Kafka
            OutboxKafkaDto dto = new OutboxKafkaDto(
                    message.getId(),
                    message.getNotificationId(),
                    message.getTitle(),
                    message.getMessageResult()
            );

            // Отправляем в Kafka
            this.kafkaProducer.sendMessageToSendingNotificationResultTopic(dto);

            // Отмечаем как опубликованное
            message.setPublished(true);
            message.setPublishedAt(LocalDateTime.now());
            this.outboxContentManager.save(message);

            log.info("Outbox message {} published successfully", message.getId());

        } catch (Exception e) {
            log.error("Error processing outbox message: {}", message.getId(), e);

            message.incrementRetryCount();

            if (message.getRetryCount() >= MAX_RETRIES) {
                log.error("Outbox message {} reached max retries, marking as dead", message.getId());
                message.setPublished(true);
                message.setPublishedAt(LocalDateTime.now());
            }

            this.outboxContentManager.save(message);
        }
    }
}