package org.jedi_bachelor.bookstatistic.accountservice.outbox.service;

import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationSettingsCreatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Setter
public class OutboxProcessorService {
    private final OutboxContentManager outboxContentManager;

    private final InteractionClient notificationInteractionClient;

    private String notificationSettingsUrl = "";

    private static final int MAX_RETRIES = 5;

    public OutboxProcessorService(
            OutboxContentManager outboxContentManager,
            @Qualifier("notificationInteractionClient") InteractionClient notificationInteractionClient) {
        this.outboxContentManager = outboxContentManager;
        this.notificationInteractionClient = notificationInteractionClient;
    }

    @Transactional
    public void process(OutboxNotificationSettingsMessage message) {
        try {
            log.info("Processing outbox message: {}", message.getId());

            NotificationSettingsCreatingDto dto = new NotificationSettingsCreatingDto(
                    message.getUserId(),
                    message.getEmailEnable(),
                    message.getEmailAddress(),
                    message.getTelegram(),
                    message.getEnableBroadcast()
            );

            switch (message.getOperation()) {
                case ADD_OPERATION:
                    this.notificationInteractionClient.sendRequest(
                            HttpMethod.POST,
                            this.notificationSettingsUrl,
                            dto
                    );
                case DELETE_OPERATION:
                    this.notificationInteractionClient.sendRequest(
                            HttpMethod.DELETE,
                            this.notificationSettingsUrl
                    );
            }

            log.info("Request is valid");

            message.markAsPublished();

            log.info("Message with ID {} marked as published", message.getId());

            this.outboxContentManager.save(message);

            log.info("Outbox message {} published successfully", message.getId());
        } catch (Exception e) {
            log.error("Error processing outbox message: {}", message.getId(), e);

            message.incrementRetryCount();

            if (message.getRetryCount() >= MAX_RETRIES) {
                log.error("Outbox message {} reached max retries, marking as dead", message.getId());
                message.markAsPublished();
            }

            this.outboxContentManager.save(message);
        }
    }
}
