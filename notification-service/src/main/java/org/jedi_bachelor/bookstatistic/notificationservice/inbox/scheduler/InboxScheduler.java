package org.jedi_bachelor.bookstatistic.notificationservice.inbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxContentManager;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.service.InboxProcessorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InboxScheduler {
    private final InboxContentManager inboxContentManager;

    private final InboxProcessorService inboxProcessorService;

    @Scheduled(fixedDelay = 5000)
    public void processInboxNotificationMessages() {
        List<InboxNotificationEntity> entities =
                this.inboxContentManager.findEntitiesWithStatus(false);

        if (entities.isEmpty()) {
            log.debug("No unprocessed inbox messages found");
            return;
        }

        log.info("Found {} unprocessed inbox messages", entities.size());

        for (InboxNotificationEntity entity : entities) {
            this.inboxProcessorService.processMessage(entity);
        }
    }
}