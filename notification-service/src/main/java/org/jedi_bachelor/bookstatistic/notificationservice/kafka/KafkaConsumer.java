package org.jedi_bachelor.bookstatistic.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.notification.NotificationCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.notificationservice.inbox.InboxContentManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final InboxContentManager contentManager;

    @KafkaListener(
            topics = "sending-notification-topic",
            groupId = "bs-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotification(@Payload NotificationCreationDto message) throws NotificationSettingsNotExistsException {
        log.info("DTO for creating notification {} has got", message);

        this.contentManager.save(message);
    }
}