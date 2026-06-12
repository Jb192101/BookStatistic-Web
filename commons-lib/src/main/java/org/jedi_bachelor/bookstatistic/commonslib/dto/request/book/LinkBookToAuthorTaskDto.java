package org.jedi_bachelor.bookstatistic.commonslib.dto.request.book;

import java.util.UUID;

public record LinkBookToAuthorTaskDto(
        UUID bookId,
        UUID authorId,
        int authorPosition
) {
}
