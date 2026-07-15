package org.jedi_bachelor.bookstatistic.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Названия топиков
    @Value("${kafka.topics.sendingNotificationResultTopic:sending-notification-result-topic}")
    private String sendingNotificationResultTopic;

    /**
     * Метод отправки сообщения в топик sending-notification-result-topic
     *
     * @param message сообщение для отправки
     */
    public void sendMessageToSendingNotificationResultTopic(Object message) {
        this.sendMessage(this.sendingNotificationResultTopic, message);
    }

    /**
     * Метод отправки сообщения в топик
     *
     * @param topic топик
     * @param message сообщение
     */
    private void sendMessage(String topic, Object message) {
        log.info("Message {} sending to topic {}", message, topic);

        this.kafkaTemplate.send(topic, message);
    }
}
