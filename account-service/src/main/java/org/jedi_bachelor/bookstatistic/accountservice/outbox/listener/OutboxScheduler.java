package org.jedi_bachelor.bookstatistic.accountservice.outbox.listener;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.configuration.InteractionPathsConfiguration;
import org.jedi_bachelor.bookstatistic.accountservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.accountservice.service.KeycloakTokenService;
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

    private final InteractionPathsConfiguration interactionPathsConfiguration;

    private final KeycloakTokenService tokenService;

    public OutboxScheduler(KafkaProducer kafkaProducer,
                           OutboxContextManager outboxContextManager,
                           @Qualifier("analyzerInteractionClient") InteractionClient analyzerClient,
                           @Qualifier("notificationInteractionClient") InteractionClient notificationClient,
                           InteractionPathsConfiguration interactionPathsConfiguration,
                           KeycloakTokenService keycloakTokenService) {
        this.kafkaProducer = kafkaProducer;
        this.outboxContextManager = outboxContextManager;
        this.analyzerClient = analyzerClient;
        this.notificationClient = notificationClient;
        this.interactionPathsConfiguration = interactionPathsConfiguration;
        this.tokenService = keycloakTokenService;
    }

    /**
     * Метод отправки сообщения в analyze-service
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendingOutboxAnalyzeMessage() {
        List<OutboxAnalyzeMessage> messages = this.outboxContextManager.findAnalyzeMessageByStatusFalse();

        for (OutboxAnalyzeMessage message : messages) {
            if (!message.getPublished()) {
                this.processAnalyzeMessage(message);
            }
        }
    }

    /**
     * Метод отправки сообщения в notification-service
     */
    @Scheduled(fixedDelay = 5000)
    public void sendingOutboxNotificationSettingsMessage() {
        List<OutboxNotificationSettingsMessage> messages =
                this.outboxContextManager.findNotificationSettingsMessageByStatusFalse();

        for (OutboxNotificationSettingsMessage message : messages) {
            if (!message.getPublished()) {
                this.processOutboxNotificationSettingsMessage(message);
            }
        }
    }

    @Transactional
    private void processAnalyzeMessage(OutboxAnalyzeMessage message) {
        log.info("Finded message to outboxing: {}", message);

        String url = this.interactionPathsConfiguration.getDeleteUserDataPath();

        this.analyzerClient.sendRequest(HttpMethod.DELETE, url + message.getUserId());

        log.info("Message with id {} has been sended", message.getId());

        message.setPublished(true);

        log.info("Message with id {} has been flag as published", message.getId());

        this.outboxContextManager.save(message);
    }

    @Transactional
    private void processOutboxNotificationSettingsMessage(OutboxNotificationSettingsMessage message) {
        log.info("Processing outbox message: {}", message);

        try {
            String url;
            HttpMethod method;
            Object body = null;
            String logAction;

            switch (message.getOperation()) {
                case ADD_OPERATION -> {
                    body = new NotificationSettingsCreatingDto(
                            message.getUserId(),
                            message.getEmailEnable(),
                            message.getEmailAddress()
                    );
                    url = this.interactionPathsConfiguration.getNotificationSettingsPostPath();
                    method = HttpMethod.POST;
                    logAction = "created";
                }
                case DELETE_OPERATION -> {
                    url = this.interactionPathsConfiguration.getNotificationSettingsPostPath() + "/" + message.getUserId();
                    method = HttpMethod.DELETE;
                    logAction = "deleted";
                }
                default -> {
                    log.error("Unknown operation: {}", message.getOperation());
                    return;
                }
            }

            log.info("Sending {} request to notification-service for user: {}",
                    message.getOperation(), message.getUserId());

            ResponseEntity<?> response = this.notificationClient.sendRequest(
                    method, url, body, this.tokenService.getAccessToken()
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully {} notification settings for user: {}", logAction, message.getUserId());
                message.setPublished(true);
                this.outboxContextManager.save(message);
                log.info("Message with id {} has been marked as published", message.getId());
            } else {
                log.error("Failed to {} notification settings. Status: {}", logAction, response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to process outbox message {}: {}", message.getId(), e.getMessage(), e);
        }
    }
}
