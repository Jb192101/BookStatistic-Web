package org.jedi_bachelor.bookstatistic.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxContentManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final InboxContentManager inboxContentManager;

    @KafkaListener(
            topics = "sending-notification-topic",
            groupId = "bs-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotification(
            @Payload NotificationCreationDto message,
            @Headers Map<String, Object> headers) {
        String key = (String) headers.get("kafka_receivedMessageKey");
        //Integer partition = (Integer) headers.get("kafka_receivedPartitionId");
        //Long offset = (Long) headers.get("kafka_receivedOffset");

        log.info("Message for creating notification has got. Key {}, content {}", key, message);

        this.inboxContentManager.save(message);
    }
}