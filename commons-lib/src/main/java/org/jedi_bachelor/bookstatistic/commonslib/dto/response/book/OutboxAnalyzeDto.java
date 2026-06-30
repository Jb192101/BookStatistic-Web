package org.jedi_bachelor.bookstatistic.commonslib.dto.response.book;

import java.time.LocalDateTime;
import java.util.UUID;

public record OutboxAnalyzeDto(
        Long id, UUID bookId, String filename, String contentType,
        Long size, Boolean published, LocalDateTime createdAt,
        LocalDateTime publishedAt, Integer retryCount
        ) {
}
