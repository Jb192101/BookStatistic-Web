package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorDto(
        UUID id,
        String firstName,
        String middleName,
        String lastName,
        LocalDate birthDay,
        String country
) {
}

