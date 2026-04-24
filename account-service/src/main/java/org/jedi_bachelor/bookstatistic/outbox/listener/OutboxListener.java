package org.jedi_bachelor.bookstatistic.outbox.listener;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.jedi_bachelor.bookstatistic.internalinteraction.InteractionClient;
import org.jedi_bachelor.bookstatistic.outbox.OutboxContextManager;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxAnalyzeMessage;
import org.jedi_bachelor.bookstatistic.outbox.entity.OutboxBookMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxListener {
    private final KafkaProducer kafkaProducer;

    private final OutboxContextManager outboxContextManager;

    @Qualifier("bookInteractionClient")
    private final InteractionClient bookClient;

    @Qualifier("analyzerInteractionClient")
    private final InteractionClient analyzerClient;

    /**
     * Метод отправки сообщения в book-service
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void prePersistOutboxBookMessage() {
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
    public void prePersistOutboxAnalyzeMessage() {
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
    public void prePersistOutboxNotificationSettingsMessage() {
        List<OutboxBookMessage> messages = this.outboxContextManager.findBookMessageByStatusFalse();

        for(OutboxBookMessage message : messages) {
            if(!message.getPublished()) {
                this.bookClient.sendRequest(HttpMethod.DELETE, "/" + message.getUserId());

                message.setPublished(true);
                outboxContextManager.save(message);
            }
        }
    }
}
