package org.jedi_bachelor.bookstatistic.commonslib.dto.request.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record LinkBookToAuthorTaskDto(
        @NotNull
        UUID bookId,

        @NotNull
        UUID authorId,

        @Min(value = 1)
        int authorPosition
) {
}
