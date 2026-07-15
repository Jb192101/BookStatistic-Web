package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.util.UUID;

public record UserBookRelationDto(
        UUID bookId, UUID userId, int readedPages
) {
}
