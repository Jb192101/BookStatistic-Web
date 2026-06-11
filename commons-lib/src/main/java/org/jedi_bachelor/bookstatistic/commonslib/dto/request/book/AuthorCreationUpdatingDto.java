package org.jedi_bachelor.bookstatistic.commonslib.dto.request.book;

import java.time.LocalDate;

public record AuthorCreationUpdatingDto(
        String firstName,
        String middleName,
        String lastName,
        LocalDate birthday,
        String country
) {
}
