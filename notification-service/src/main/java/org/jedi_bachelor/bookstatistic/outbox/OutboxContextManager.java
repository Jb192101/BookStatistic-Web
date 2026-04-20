package org.jedi_bachelor.bookstatistic.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxEmailMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxEmailMessageRepository;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxKafkaMessageRepository;
import org.springframework.stereotype.Component;

/**
 * Класс для управления контекстом outbox-сообщений
 */

@Component
@RequiredArgsConstructor
public class OutboxContextManager {
    private final OutboxEmailMessageRepository outboxEmailMessageRepository;

    private final OutboxKafkaMessageRepository outboxKafkaMessageRepository;

    /**
     * Метод сохранения нового outbox-сообщения для почты
     *
     * @param message сообщение
     */
    public void addOutboxEmailMessage(OutboxEmailMessage message) {
        this.outboxEmailMessageRepository.save(message);
    }

    /**
     * Метод сохранения нового outbox-сообщения для Kafka
     *
     * @param message сообщение
     */
    public void addOutboxKafkaMessage(OutboxKafkaMessage message) {
        this.outboxKafkaMessageRepository.save(message);
    }
}
