package org.jedi_bachelor.bookstatistic.bookservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessageToSendingNotificationTopic(Object message) {
        this.sendMessage("sending-notification-topic", message);
    }

    public void sendMessageToBookTextAnalyzeTopic(Object message) {
        this.sendMessage("book-text-analyze-topic", message);
    }

    private void sendMessage(String topic, Object message) {
        log.info("Sending message to topic {} with message content {}", topic, message);

        this.kafkaTemplate.send(topic, message);
    }
}
