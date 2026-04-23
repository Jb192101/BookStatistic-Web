package org.jedi_bachelor.bookstatistic.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Названия топиков
    @Value("${kafka.topics.sending-notification-result-topic}")
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
        this.kafkaTemplate.send(topic, message);
    }
}
