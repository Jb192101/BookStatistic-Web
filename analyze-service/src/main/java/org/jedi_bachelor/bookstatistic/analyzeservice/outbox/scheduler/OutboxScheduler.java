package org.jedi_bachelor.bookstatistic.analyzeservice.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.entity.OutboxAnalyzeResultEntity;
import org.jedi_bachelor.bookstatistic.analyzeservice.outbox.service.OutboxProcessorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxContentManager outboxContentManager;

    private final OutboxProcessorService outboxProcessorService;

    @Scheduled(fixedDelay = 5000)
    public void processOutboxNotificationMessages() {
        List<OutboxAnalyzeResultEntity> entities =
                this.outboxContentManager.findEntitiesWithStatus(false);

        if (entities.isEmpty()) {
            log.debug("No unprocessed outbox messages found");
            return;
        }

        log.info("Found {} unprocessed outbox messages", entities.size());

        for (OutboxAnalyzeResultEntity entity : entities) {
            this.outboxProcessorService.processOneOutboxMessage(entity);
        }
    }
}
