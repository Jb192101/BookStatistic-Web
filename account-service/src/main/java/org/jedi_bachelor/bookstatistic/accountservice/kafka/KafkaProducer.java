package org.jedi_bachelor.bookstatistic.accountservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.sendingNotificationTopic:sending-notification-topic}")
    private String sendingNotificationTopicName;

    public void sendMessageToSendingNotificationTopic(Object message) {
        this.sendMessage(this.sendingNotificationTopicName, message);
    }

    private void sendMessage(String topic, Object message) {
        log.info("Sending message to topic {} with message content {}", topic, message);

        this.kafkaTemplate.send(topic, message);
    }
}
