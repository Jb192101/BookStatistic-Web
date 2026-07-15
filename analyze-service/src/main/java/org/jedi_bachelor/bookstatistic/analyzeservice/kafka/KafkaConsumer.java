package org.jedi_bachelor.bookstatistic.analyzeservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.converter.KafkaTextConverter;
import org.jedi_bachelor.bookstatistic.analyzeservice.inbox.InboxContentManager;
import org.jedi_bachelor.bookstatistic.analyzeservice.inbox.entity.InboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    @Value("${kafka.topics.book-text-analyze-topic}")
    private String bookTextAnalyzeTopic;

    private final InboxContentManager inboxContentManager;

    private final KafkaTextConverter kafkaTextConverter;

    @KafkaListener(
            topics = "${kafka.topics.book-text-analyze-topic}",
            groupId = "bs-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotification(
            @Payload KafkaTextAnalyzeDto message,
            @Headers Map<String, Object> headers
    ) {
        String key = (String) headers.get("kafka_receivedMessageKey");
        //Integer partition = (Integer) headers.get("kafka_receivedPartitionId");
        //Long offset = (Long) headers.get("kafka_receivedOffset");

        log.info("Message to for analyze text has got, key {}, content {}", key, message);

        InboxAnalyzeEntity entity = this.kafkaTextConverter.convert(message);

        this.inboxContentManager.save(entity);
    }
}
