package org.jedi_bachelor.bookstatistic.analyzeservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.redis.entity.TextFile;
import org.jedi_bachelor.bookstatistic.analyzeservice.service.AnalyzeService;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final AnalyzeService analyzeService;

    @KafkaListener(topics = "book-text-analyze-topic", groupId = "bs-group")
    public void handleBookTextAnalyzeTopic(KafkaTextAnalyzeDto message) {
        log.info("Message has been received {}, {}, {}. ID of book: {}", message.id(), message.filename(), message.size(), message.bookId());

        this.analyzeService.analyzeTextFile(message);
    }
}
