package org.jedi_bachelor.bookstatistic.notificationservice.inbox.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxContentManager;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;
import org.jedi_bachelor.bookstatistic.notificationservice.service.NotificationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboxScheduler {
    private final InboxContentManager inboxContentManager;

    private final NotificationService notificationService;

    private static final int COUNT_OF_RETRIES_TO_PROCESS = 5;

    @Scheduled(fixedDelay = 5000)
    public void processInboxNotificationMessages() {
        List<InboxNotificationEntity> entities =
                this.inboxContentManager.findEntitiesWithStatus(false);

        for(InboxNotificationEntity entity : entities) {
            this.processNotificationMessage(entity);
        }
    }

    @Transactional
    public void processNotificationMessage(InboxNotificationEntity entity) {
        try {
            log.info("Message {} started to process", entity);

            // Перевод inbox-сущности в DTO
            NotificationCreationDto dto = new NotificationCreationDto(
                    entity.getUserId(),
                    entity.getType().toString(),
                    entity.getNotificationTitle(),
                    entity.getMessage()
            );

            // Добавление нового уведомления
            this.notificationService.addNewNotification(dto);

        } catch (NotificationSettingsNotExistsException e) {
            log.warn("Notification settings of user with ID {} didn't found", entity.getUserId());

            entity.setProcessed(true);
            entity.setProcessedAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Technical error processing inbox message: {}, error: {}", entity.getId(), e.getMessage());

            entity.incrementRetryCount();

            if (entity.getRetryCount() >= COUNT_OF_RETRIES_TO_PROCESS) {
                log.error("Message {} reached max retries, marking as dead", entity.getId());
                entity.setProcessed(true);
            }
        }
    }
}