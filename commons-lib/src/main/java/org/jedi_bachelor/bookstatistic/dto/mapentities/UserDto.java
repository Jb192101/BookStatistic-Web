package org.jedi_bachelor.bookstatistic.dto.mapentities;

import java.util.UUID;

public record UserDto(
    UUID id,
    String name,
    String hashPassword,
    String role,
    Boolean enableEmail,
    String email,
    String telegramAddress
) {
}
