package org.jedi_bachelor.bookstatistic.bookservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.inbox.OutboxKafkaMessage;
import org.jedi_bachelor.bookstatistic.bookservice.service.BookService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final BookService bookService;

    @KafkaListener(topics = "sending-notification-result-topic", groupId = "bs-group")
    public void handleSendingNotificationResultTopic(@Payload OutboxKafkaMessage message) {
        log.info("Message with result of notificating has been got: {}", message);
    }
}
