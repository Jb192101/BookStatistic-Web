package org.jedi_bachelor.bookstatistic.commonslib.dto.response.book;

import java.util.UUID;

public record UserReadingStat(
        UUID userId,
        int allAmount,
        int fullReadedBooksCount,
        int partialReadedBooksCount,
        int abandonedBooksCount,
        int gotAndNotReadedBooksCount
) {
}
