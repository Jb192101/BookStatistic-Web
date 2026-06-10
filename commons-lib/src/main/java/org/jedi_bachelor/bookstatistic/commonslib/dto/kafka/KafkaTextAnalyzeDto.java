package org.jedi_bachelor.bookstatistic.commonslib.dto.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

public record KafkaTextAnalyzeDto(
        UUID bookId,
        String filename,
        String content,
        String contentType,
        long size,
        LocalDateTime uploadTime
) {
}
