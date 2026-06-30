package org.jedi_bachelor.bookstatistic.notificationservice.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.entity.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.notificationservice.outbox.service.OutboxProcessorService;
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
    public void processOutboxMessages() {
        List<OutboxKafkaMessage> messages =
                this.outboxContentManager.findKafkaMessageByStatusFalse();

        if (messages.isEmpty()) {
            log.debug("No unprocessed outbox messages found");
            return;
        }

        log.info("Found {} unprocessed outbox messages", messages.size());

        for (OutboxKafkaMessage message : messages) {
            this.outboxProcessorService.processOutboxMessage(message);
        }
    }
}
