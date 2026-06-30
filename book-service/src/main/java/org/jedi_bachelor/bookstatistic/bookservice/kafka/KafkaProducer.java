package org.jedi_bachelor.bookstatistic.bookservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sending-notification-topic:sending-notification-topic}")
    private String sendingNotificationTopicName;

    @Value("${kafka.topics.book-text-analyze-topic:book-text-analyze-topic}")
    private String bookTextAnalyzeTopicName;

    public void sendMessageToSendingNotificationTopic(Object message) {
        this.sendMessage(this.sendingNotificationTopicName, message);
    }

    public void sendMessageToBookTextAnalyzeTopic(Object message) {
        this.sendMessage(this.bookTextAnalyzeTopicName, message);
    }

    private void sendMessage(String topic, Object message) {
        String messageKey = this.generateKey();

        log.info("Sending message to topic {} with message content {}, key: {}", topic, message, messageKey);

        this.kafkaTemplate.send(topic, messageKey, message);
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }
}