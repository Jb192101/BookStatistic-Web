package org.jedi_bachelor.bookstatistic.commonslib.dto.request.book;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookAuthorRelationKey(
        @NotNull
        UUID bookId,

        @NotNull
        UUID authorId
) {
}
