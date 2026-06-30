package org.jedi_bachelor.bookstatistic.commonslib.dto.request.book;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserBookRelationCreatingUpdatingDto(
        UUID userId,
        UUID bookId,

        @NotNull
        int readedPages
) {
}
