package org.jedi_bachelor.bookstatistic.kafka;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.service.BookService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final BookService bookService;

    @KafkaListener(topics = "sending-notification-result-topic", groupId = "bs-group")
    public void handleSendingNotificationResultTopic() {

    }
}
