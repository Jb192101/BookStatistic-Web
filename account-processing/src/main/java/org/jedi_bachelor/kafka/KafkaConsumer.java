package org.jedi_bachelor.kafka;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.kafka.dto.BookRatingDto;
import org.jedi_bachelor.kafka.dto.KafkaDtoMessage;
import org.jedi_bachelor.service.AccountBookService;
import org.jedi_bachelor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    @Autowired
    private final AccountService accountService;

    @Autowired
    private final AccountBookService accountBookService;

    @KafkaListener(topics = "book-topic", groupId = "ms-group")
    public void handleUpdatingNewReadedPages(KafkaDtoMessage message) {
        try {
            accountBookService.updateRelationship((BookRatingDto) message);

            accountService.updateUserRating(message);
        } catch (Exception e) {
        }
    }
}
