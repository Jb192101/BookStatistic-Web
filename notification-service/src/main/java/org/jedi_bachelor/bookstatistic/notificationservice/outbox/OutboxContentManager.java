package org.jedi_bachelor.bookstatistic.notificationservice.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.notification.OutboxNotificationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.mapper.OutboxNotificationMapper;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.repository.OutboxKafkaMessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс для управления контекстом outbox-сообщений
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxContentManager {
    private final OutboxKafkaMessageRepository outboxKafkaMessageRepository;

    private final OutboxNotificationMapper outboxNotificationMapper;

    public List<OutboxNotificationDto> findAllOutboxNotifications() {
        List<OutboxKafkaMessage> messages = this.outboxKafkaMessageRepository.findAll();

        return this.outboxNotificationMapper.toDtoList(messages);
    }

    /**
     * Метод сохранения нового outbox-сообщения для Kafka
     *
     * @param message сообщение
     */
    public void save(OutboxKafkaMessage message) {
        this.outboxKafkaMessageRepository.save(message);

        log.info("Outbot kafka message {} has saved", message);
    }

    /**
     * Поиск всех outbox сообщений для kafka со статусом false
     * @return список outbox сообщений
     */
    public List<OutboxKafkaMessage> findKafkaMessageByStatusFalse() {
        return this.outboxKafkaMessageRepository.findByPublished(false);
    }
}
