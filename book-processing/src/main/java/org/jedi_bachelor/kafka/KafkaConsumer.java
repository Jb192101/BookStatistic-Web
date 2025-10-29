package org.jedi_bachelor.kafka;

import org.jedi_bachelor.kafka.dto.KafkaDtoMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @KafkaListener(topics = "book-topic", groupId = "ms-group")
    public void handleUpdatingNewReadedPages(KafkaDtoMessage message) {
    }
}
