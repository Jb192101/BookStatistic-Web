package org.jedi_bachelor.bookstatistic.accountservice.outbox.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.accountservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxBookMessage;
import org.jedi_bachelor.bookstatistic.accountservice.outbox.entity.OutboxNotificationSettingsMessage;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final KafkaProducer kafkaProducer;

    private final OutboxContextManager outboxContextManager;

    @Qualifier("bookInteractionClient")
    private final InteractionClient bookClient;

    @Qualifier("analyzerInteractionClient")
    private final InteractionClient analyzerClient;

    @Qualifier("notificationInteractionClient")
    private final InteractionClient notificationClient;

    /**
     * Метод отправки сообщения в book-service
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void sendingOutboxBookMessage() {
        List<OutboxBookMessage> messages = this.outboxContextManager.findBookMessageByStatusFalse();

        for(OutboxBookMessage message : messages) {
            if(!message.getPublished()) {
                this.bookClient.sendRequest(HttpMethod.DELETE, "/" + message.getUserId());

                message.setPublished(true);
                outboxContextManager.save(message);
            }
        }
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
                this.analyzerClient.sendRequest(HttpMethod.DELETE, "/" + message.getUserId());

                message.setPublished(true);
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

        for(OutboxNotificationSettingsMessage message : messages) {
            if(!message.getPublished()) {
                HttpMethod method = null;
                switch (message.getOperation()) {
                    // Если добавляем настройки уведомлений
                    case ADD_OPERATION -> {
                        method = HttpMethod.POST;
                    }

                    // Если удаляем настройки уведомлений
                    case DELETE_OPERATION -> {
                        method = HttpMethod.DELETE;
                    }
                }

                if(method == null) {
                    continue;
                }

                this.notificationClient.sendRequest(method, "/notification-settings/" + message.getUserId());

                message.setPublished(true);
                this.outboxContextManager.save(message);
            }
        }
    }

}
