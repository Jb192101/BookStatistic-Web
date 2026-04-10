package org.jedi_bachelor.bookstatistic.kafka;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final NotificationService notificationService;

    @KafkaListener(topics = "sending-notification-topic", groupId = "bs-group")
    public void handleNotification(NotificationCreationDto message) {

    }
}
