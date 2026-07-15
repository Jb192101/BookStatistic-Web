package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseDto(
        UUID userId,
        UUID bookId,
        int stars,
        String responseText,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
