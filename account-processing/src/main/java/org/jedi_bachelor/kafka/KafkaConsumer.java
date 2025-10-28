package org.jedi_bachelor.kafka;

import org.jedi_bachelor.kafka.dto.KafkaDtoMessage;
import org.jedi_bachelor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    @Autowired
    private AccountService accountService;

    @KafkaListener(topics = "book-topic", groupId = "ms-group")
    public void handleUpdatingNewReadedPages(KafkaDtoMessage message) {
        try {

            accountService.updateUserRating(message);
        } catch (Exception e) {
        }
    }
}
