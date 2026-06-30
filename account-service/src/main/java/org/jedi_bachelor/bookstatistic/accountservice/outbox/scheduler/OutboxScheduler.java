package org.jedi_bachelor.bookstatistic.accountservice.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.service.OutboxProcessorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxContentManager outboxContentManager;

    private final OutboxProcessorService processorService;

    /**
     * Метод отправки сообщения в notification-service
     */
    @Scheduled(fixedDelay = 5000)
    public void sendingOutboxNotificationSettingsMessage() {
        List<OutboxNotificationSettingsMessage> messages =
                this.outboxContentManager.findNotificationSettingsMessageByStatusFalse();

        for (OutboxNotificationSettingsMessage message : messages) {
            this.processorService.process(message);
        }
    }
}
