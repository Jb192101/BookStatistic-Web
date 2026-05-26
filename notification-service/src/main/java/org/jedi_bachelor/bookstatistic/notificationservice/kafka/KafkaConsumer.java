package org.jedi_bachelor.bookstatistic.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final NotificationService notificationService;

    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "sending-notification-topic",
            groupId = "bs-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotification(NotificationCreationDto message) {
        log.info("DTO for creating notification {} has got", message);

        this.notificationService.addNewNotification(message);
    }
}