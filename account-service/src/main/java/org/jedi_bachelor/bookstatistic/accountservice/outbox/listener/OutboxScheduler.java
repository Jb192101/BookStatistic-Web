package org.jedi_bachelor.bookstatistic.accountservice.outbox.listener;

import jakarta.transaction.Transactional;
import org.jedi_bachelor.bookstatistic.accountservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxScheduler {
    private final KafkaProducer kafkaProducer;

    private final OutboxContextManager outboxContextManager;

    private final InteractionClient bookClient;

    private final InteractionClient analyzerClient;

    private final InteractionClient notificationClient;

    public OutboxScheduler(KafkaProducer kafkaProducer,
                           OutboxContextManager outboxContextManager,
                           @Qualifier("bookInteractionClient") InteractionClient bookClient,
                           @Qualifier("analyzerInteractionClient") InteractionClient analyzerClient,
                           @Qualifier("notificationInteractionClient") InteractionClient notificationClient) {
        this.kafkaProducer = kafkaProducer;
        this.outboxContextManager = outboxContextManager;
        this.bookClient = bookClient;
        this.analyzerClient = analyzerClient;
        this.notificationClient = notificationClient;
    }

    /**
     * Метод отправки сообщения в book-service
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendingOutboxBookMessage() {
        List<OutboxBookMessage> messages = this.outboxContextManager.findBookMessageByStatusFalse();

        for(OutboxBookMessage message : messages) {
            if(!message.getPublished()) {
                this.bookClient.sendRequest(HttpMethod.DELETE, "/" + message.getUserId());

                message.setPublished(true);
                outboxContextManager.save(message);
            }
        }
    }

    /**
     * Метод отправки сообщения в analyze-service
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendingOutboxAnalyzeMessage() {
        List<OutboxAnalyzeMessage> messages = this.outboxContextManager.findAnalyzeMessageByStatusFalse();

        for(OutboxAnalyzeMessage message : messages) {
            if(!message.getPublished()) {
                this.analyzerClient.sendRequest(HttpMethod.DELETE, "/" + message.getUserId());

                message.setPublished(true);
                outboxContextManager.save(message);
            }
        }
    }

    /**
     * Метод отправки сообщения в notification-service
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendingOutboxNotificationSettingsMessage() {
        List<OutboxNotificationSettingsMessage> messages =
                this.outboxContextManager.findNotificationSettingsMessageByStatusFalse();

        for(OutboxNotificationSettingsMessage message : messages) {
            if(!message.getPublished()) {
                switch (message.getOperation()) {
                    // Если добавляем настройки уведомлений
                    case ADD_OPERATION -> {
                        NotificationSettingsCreatingDto dto = new NotificationSettingsCreatingDto(
                                message.getUserId(),
                                message.getEmailEnable(),
                                message.getEmailAddress()
                        );

                        this.notificationClient.sendRequest(
                                HttpMethod.POST,
                                "/notification-settings/" + message.getUserId(),
                                dto
                        );

                        message.setPublished(true);
                        this.outboxContextManager.save(message);
                    }

                    // Если удаляем настройки уведомлений
                    case DELETE_OPERATION -> {
                        this.notificationClient.sendRequest(HttpMethod.DELETE, "/notification-settings/" + message.getUserId());

                        message.setPublished(true);
                        this.outboxContextManager.save(message);
                    }
                }
            }
        }
    }

}
