package org.jedi_bachelor.bookstatistic.bookservice.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxNotificationEntity;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.repository.OutboxAnalyzeRepository;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.repository.OutboxNotificationEntityRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxContentManager {
    private final OutboxNotificationEntityRepository outboxNotificationEntityRepository;

    private final OutboxAnalyzeRepository outboxAnalyzeRepository;

    /**
     * Метод добавления сущности сообщения для notification-service
     *
     * @param entity сущность сообщения
     * @return сущность сообщения
     */
    public OutboxNotificationEntity save(OutboxNotificationEntity entity) {
        return this.outboxNotificationEntityRepository.save(entity);
    }

    /**
     * Метод добавления сущности сообщения для analyze-service
     *
     * @param dto DTO для сохранения
     * @return сущность сообщения
     */
    public OutboxAnalyzeEntity save(KafkaTextAnalyzeDto dto) {
        OutboxAnalyzeEntity entity = this.convertDtoToAnalyzeEntity(dto);

        return this.outboxAnalyzeRepository.save(entity);
    }

    /**
     * Метод добавления сущности сообщения для analyze-service
     *
     * @param entity сущность для сохранения
     * @return сущность сообщения
     */
    public OutboxAnalyzeEntity save(OutboxAnalyzeEntity entity) {
        return this.outboxAnalyzeRepository.save(entity);
    }

    private OutboxAnalyzeEntity convertDtoToAnalyzeEntity(KafkaTextAnalyzeDto dto) {
        OutboxAnalyzeEntity entity = new OutboxAnalyzeEntity();
        entity.setDto(dto);

        return entity;
    }
}
