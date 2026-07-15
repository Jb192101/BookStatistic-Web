package org.jedi_bachelor.bookstatistic.commonslib.dto.request.book;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ResponseCreateUpdateDto(
        @NotNull
        UUID bookId,

        @NotNull
        UUID userId,

        @NotNull
        @Min(value = 10)
        String responseText,

        @NotNull
        @Min(value = 1)
        @Max(value = 5)
        int countOfStars
) {
}
