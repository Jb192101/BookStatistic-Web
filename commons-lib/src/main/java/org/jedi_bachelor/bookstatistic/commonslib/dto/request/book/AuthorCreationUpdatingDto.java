package org.jedi_bachelor.bookstatistic.commonslib.dto.request.book;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record AuthorCreationUpdatingDto(
        String firstName,
        String middleName,
        String lastName,

        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate birthday,
        String country
) {
}
