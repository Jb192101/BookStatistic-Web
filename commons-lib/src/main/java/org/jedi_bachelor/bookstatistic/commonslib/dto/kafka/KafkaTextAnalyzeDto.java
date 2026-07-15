package org.jedi_bachelor.bookstatistic.commonslib.dto.kafka;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record KafkaTextAnalyzeDto(
        @NotNull
        UUID bookId,

        @NotNull
        String filename,

        @NotNull
        String content,

        @NotNull
        String contentType,

        @NotNull
        long size
) {
}
