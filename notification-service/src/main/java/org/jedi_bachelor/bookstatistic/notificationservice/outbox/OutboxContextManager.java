package org.jedi_bachelor.bookstatistic.notificationservice.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.repository.OutboxEmailMessageRepository;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.repository.OutboxKafkaMessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс для управления контекстом outbox-сообщений
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxContextManager {
    private final OutboxEmailMessageRepository outboxEmailMessageRepository;

    private final OutboxKafkaMessageRepository outboxKafkaMessageRepository;

    /**
     * Метод сохранения нового outbox-сообщения для почты
     *
     * @param message сообщение
     */
    public void saveOutboxEmailMessage(OutboxEmailMessage message) {
        this.outboxEmailMessageRepository.save(message);

        log.info("Outbot email message {} has saved", message);
    }

    /**
     * Метод сохранения нового outbox-сообщения для Kafka
     *
     * @param message сообщение
     */
    public void saveOutboxKafkaMessage(OutboxKafkaMessage message) {
        this.outboxKafkaMessageRepository.save(message);

        log.info("Outbot kafka message {} has saved", message);
    }

    /**
     * Поиск всех outbox сообщений для email со статусом false
     * @return список outbox сообщений
     */
    public List<OutboxEmailMessage> findEmailMessageByStatusFalse() {
        return this.outboxEmailMessageRepository.findByPublishedFalse();
    }

    /**
     * Поиск всех outbox сообщений для kafka со статусом false
     * @return список outbox сообщений
     */
    public List<OutboxKafkaMessage> findKafkaMessageByStatusFalse() {
        return this.outboxKafkaMessageRepository.findByPublishedFalse();
    }
}
