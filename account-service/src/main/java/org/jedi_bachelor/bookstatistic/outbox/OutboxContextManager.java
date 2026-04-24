package org.jedi_bachelor.bookstatistic.outbox;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxBookMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxAnalyzeMessageRepository;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxBookMessageRepository;
import org.jedi_bachelor.bookstatistic.outbox.repository.OutboxNotificationSettingsMessageRepository;
import org.springframework.stereotype.Component;

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
        addBookMessage(userId, "delete");
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
        addBookMessage(userId, "add");
    }

    /**
     * Метод на добавление сообщения на удаление
     *
     * @param userId ID пользователя
     */
    public void addAnalyzeMessageToDelete(UUID userId) {
        addAnalyzeMessage(userId, "delete");
    }

    /**
     * Метод добавления сообщения на создание сущности
     *
     * @param userId ID пользователя
     */
    public void addAnalyzeMessageToAdd(UUID userId) {
        addAnalyzeMessage(userId, "add");
    }

    private void addAnalyzeMessage(UUID userId, String action) {
        OutboxAnalyzeMessage message = new OutboxAnalyzeMessage();
        message.setUserId(userId);
        message.setAction(action);

        this.outboxAnalyzeMessageRepository.save(message);
    }

    private void addBookMessage(UUID userId, String action) {
        OutboxBookMessage message = new OutboxBookMessage();
        message.setUserId(userId);
        message.setAction(action);

        this.outboxBookMessageRepository.save(message);
    }
}
