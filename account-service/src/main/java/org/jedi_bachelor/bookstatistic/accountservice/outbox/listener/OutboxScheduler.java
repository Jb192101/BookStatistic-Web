package org.jedi_bachelor.bookstatistic.accountservice.outbox.listener;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OutboxScheduler {
    private final KafkaProducer kafkaProducer;

    private final OutboxContextManager outboxContextManager;

    private final InteractionClient analyzerClient;

    private final InteractionClient notificationClient;

    public OutboxScheduler(KafkaProducer kafkaProducer,
                           OutboxContextManager outboxContextManager,
                           @Qualifier("analyzerInteractionClient") InteractionClient analyzerClient,
                           @Qualifier("notificationInteractionClient") InteractionClient notificationClient) {
        this.kafkaProducer = kafkaProducer;
        this.outboxContextManager = outboxContextManager;
        this.analyzerClient = analyzerClient;
        this.notificationClient = notificationClient;
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
                log.info("Finded message to outboxing: {}", message);

                this.analyzerClient.sendRequest(HttpMethod.DELETE, "/" + message.getUserId());

                log.info("Message with id {} has been sended", message.getId());

                message.setPublished(true);

                log.info("Message with id {} has been flag as published", message.getId());

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

        for (OutboxNotificationSettingsMessage message : messages) {
            if (!message.getPublished()) {
                log.info("Processing outbox message: {}", message);

                try {
                    switch (message.getOperation()) {
                        case ADD_OPERATION -> {
                            NotificationSettingsCreatingDto dto = new NotificationSettingsCreatingDto(
                                    message.getUserId(),
                                    message.getEmailEnable(),
                                    message.getEmailAddress()
                            );

                            String url = "/v1/notifications/notification-settings";

                            log.info("Sending ADD request to notification-service for user: {}", message.getUserId());

                            ResponseEntity<?> response = this.notificationClient.sendRequest(
                                    HttpMethod.POST,
                                    url,
                                    dto
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                log.info("Successfully created notification settings for user: {}", message.getUserId());
                            } else {
                                log.error("Failed to create notification settings. Status: {}", response.getStatusCode());
                                continue;
                            }
                        }

                        case DELETE_OPERATION -> {
                            String url = "/v1/notifications/notification-settings/" + message.getUserId();

                            log.info("Sending DELETE request to notification-service for user: {}", message.getUserId());

                            ResponseEntity<?> response = this.notificationClient.sendRequest(
                                    HttpMethod.DELETE,
                                    url
                            );

                            if (response.getStatusCode().is2xxSuccessful()) {
                                log.info("Successfully deleted notification settings for user: {}", message.getUserId());
                            } else {
                                log.error("Failed to delete notification settings. Status: {}", response.getStatusCode());
                                continue;
                            }
                        }
                    }

                    message.setPublished(true);
                    this.outboxContextManager.save(message);
                    log.info("Message with id {} has been marked as published", message.getId());

                } catch (Exception e) {
                    log.error("Failed to process outbox message {}: {}", message.getId(), e.getMessage(), e);
                }
            }
        }
    }
}
