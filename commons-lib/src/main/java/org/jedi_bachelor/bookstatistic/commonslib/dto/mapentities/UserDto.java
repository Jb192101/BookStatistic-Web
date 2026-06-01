package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String keycloakSub,
    String name,
    String hashPassword,
    String language,
    LocalDateTime createdAt,
    LocalDate birthDay
) {
}
