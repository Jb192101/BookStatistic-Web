package org.jedi_bachelor.bookstatistic.bookservice.dto;

import lombok.Builder;

@Builder
public record BookMetrics(
        int pages,
        int pagesByChars,
        int pagesByWords,
        int totalChars,
        int totalWords,
        int readingTimeMinutes
) {
}
