package org.jedi_bachelor.bookstatistic.notificationservice.inbox.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.NotificationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxContentManager;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboxProcessorService {
    private final NotificationService notificationService;

    private final InboxContentManager inboxContentManager;

    private final OutboxContentManager outboxContentManager;

    private static final int COUNT_OF_RETRIES_TO_PROCESS = 5;

    @Transactional
    public void processMessage(InboxNotificationEntity entity) {
        try {
            log.info("Processing inbox message: {}", entity.getId());

            NotificationCreationDto dto = new NotificationCreationDto(
                    entity.getUserId(),
                    entity.getType().name(),
                    entity.getNotificationTitle(),
                    entity.getMessage()
            );

            NotificationDto notificationDto = this.notificationService.addNewNotification(dto);

            // Сохраняем изменения
            entity.setProcessed(true);
            entity.setProcessedAt(LocalDateTime.now());
            this.inboxContentManager.save(entity);

            log.info("Message {} processed successfully", entity.getId());

            // Сохраняем в outbox сведения о том, что сообщение успешно отправлено
            OutboxKafkaMessage message = new OutboxKafkaMessage();
            message.setTitle(entity.getNotificationTitle());
            message.setNotificationId(notificationDto.id());
            message.setMessageResult("Notification succesfully created");

            this.outboxContentManager.save(message);

        } catch (NotificationSettingsNotExistsException e) {
            log.warn("Settings not found for user: {}", entity.getUserId());
            entity.setProcessed(true);
            entity.setProcessedAt(LocalDateTime.now());
            this.inboxContentManager.save(entity);

        } catch (Exception e) {
            log.error("Error processing message: {}", entity.getId(), e);

            entity.incrementRetryCount();

            if (entity.getRetryCount() >= COUNT_OF_RETRIES_TO_PROCESS) {
                log.error("Message {} reached max retries, marking as dead", entity.getId());
                entity.setProcessed(true);
                entity.setProcessedAt(LocalDateTime.now());
            }

            this.inboxContentManager.save(entity);
        }
    }
}
