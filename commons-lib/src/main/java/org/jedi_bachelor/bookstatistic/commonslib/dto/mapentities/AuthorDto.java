package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorDto(UUID id,
                        String firstName,
                        String middleName,
                        String lastName,
                        LocalDate birthday,
                        String country) {
}

