package org.jedi_bachelor.bookstatistic.bookservice.outbox.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxProcessorService {
    private final KafkaProducer kafkaProducer;

    private final OutboxContentManager outboxContentManager;

    private static final int MAX_RETRIES = 5;

    @Transactional
    public void processAnalyzeMessage(OutboxAnalyzeEntity entity) {
        try {
            log.info("Processing outbox message: {}", entity.getId());
            KafkaTextAnalyzeDto dto = new KafkaTextAnalyzeDto(
                    entity.getBookId(),
                    entity.getFilename(),
                    entity.getContent(),
                    entity.getContentType(),
                    entity.getSize()
            );

            this.kafkaProducer.sendMessageToBookTextAnalyzeTopic(dto);

            entity.setPublished(true);
            entity.setPublishedAt(LocalDateTime.now());

            this.outboxContentManager.save(entity);

            log.info("Outbox message {} published successfully", entity.getId());
        } catch (Exception e) {
            log.error("Error processing outbox message: {}", entity.getId(), e);

            entity.incrementRetryCount();

            if (entity.getRetryCount() >= MAX_RETRIES) {
                log.error("Outbox message {} reached max retries, marking as dead", entity.getId());
                entity.setPublished(true);
                entity.setPublishedAt(LocalDateTime.now());
            }

            this.outboxContentManager.save(entity);
        }
    }
}
