package org.jedi_bachelor.bookstatistic.bookservice.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.service.OutboxProcessorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {
    private final OutboxContentManager outboxContentManager;

    private final OutboxProcessorService outboxProcessorService;

    @Scheduled(fixedDelay = 5000)
    public void processOutboxAnalyzeMessages() {
        List<OutboxAnalyzeEntity> messages =
                this.outboxContentManager.findAnalyzeMessageByStatusFalse();

        if (messages.isEmpty()) {
            log.debug("No unprocessed outbox messages found");
            return;
        }

        log.info("Found {} unprocessed outbox messages", messages.size());

        for (OutboxAnalyzeEntity message : messages) {
            this.outboxProcessorService.processAnalyzeMessage(message);
        }
    }
}
