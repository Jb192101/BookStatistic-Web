package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.util.UUID;

public record BookAuthorRelationDto(
        UUID bookId,
        UUID authorId,
        int authorPosition
) {
}
