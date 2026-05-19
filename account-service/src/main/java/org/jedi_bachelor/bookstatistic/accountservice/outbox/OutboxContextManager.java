package org.jedi_bachelor.bookstatistic.accountservice.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxBookMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.repository.OutboxAnalyzeMessageRepository;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.repository.OutboxNotificationSettingsMessageRepository;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.repository.OutboxBookMessageRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxContextManager {
    private final OutboxNotificationSettingsMessageRepository outboxNotificationSettingsMessageRepository;

    private final OutboxAnalyzeMessageRepository outboxAnalyzeMessageRepository;

    private final OutboxBookMessageRepository outboxBookMessageRepository;

    /**
     * Метод добавления сообщения книг на удаление
     *
     * @param userId ID пользователя
     */
    public void addBookMessageToDelete(UUID userId) {
        this.addBookMessage(userId, OutboxOperation.DELETE_OPERATION);
    }

    public List<OutboxBookMessage> findBookMessageByStatusFalse() {
        return this.outboxBookMessageRepository.findByPublishedFalse();
    }

    public List<OutboxNotificationSettingsMessage> findNotificationSettingsMessageByStatusFalse() {
        return this.outboxNotificationSettingsMessageRepository.findByPublishedFalse();
    }

    public List<OutboxAnalyzeMessage> findAnalyzeMessageByStatusFalse() {
        return this.outboxAnalyzeMessageRepository.findByPublishedFalse();
    }

    public void save(OutboxAnalyzeMessage message) {
        this.outboxAnalyzeMessageRepository.save(message);
    }

    public void save(OutboxBookMessage message) {
        this.outboxBookMessageRepository.save(message);
    }

    public void save(OutboxNotificationSettingsMessage message) {
        this.outboxNotificationSettingsMessageRepository.save(message);
    }

    /**
     * Метод добавления сообщения книг на создание
     *
     * @param userId ID пользователя
     */
    public void addBookMessageToAdd(UUID userId) {
        this.addBookMessage(userId, OutboxOperation.ADD_OPERATION);
    }

    /**
     * Метод на добавление сообщения на удаление
     *
     * @param userId ID пользователя
     */
    public void addAnalyzeMessageToDelete(UUID userId) {
        this.addAnalyzeMessage(userId, OutboxOperation.DELETE_OPERATION);
    }

    /**
     * Метод добавления сообщения на создание сущности
     *
     * @param userId ID пользователя
     */
    public void addAnalyzeMessageToAdd(UUID userId) {
        this.addAnalyzeMessage(userId, OutboxOperation.ADD_OPERATION);
    }

    private void addAnalyzeMessage(UUID userId, OutboxOperation action) {
        OutboxAnalyzeMessage message = new OutboxAnalyzeMessage();
        message.setUserId(userId);
        message.setAction(action);
        message.setCreatedAt(LocalDateTime.now());

        this.outboxAnalyzeMessageRepository.save(message);
    }

    private void addBookMessage(UUID userId, OutboxOperation action) {
        OutboxBookMessage message = new OutboxBookMessage();
        message.setUserId(userId);
        message.setAction(action);
        message.setCreatedAt(LocalDateTime.now());

        this.outboxBookMessageRepository.save(message);
    }
}
