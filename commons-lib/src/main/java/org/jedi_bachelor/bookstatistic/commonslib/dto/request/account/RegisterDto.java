package org.jedi_bachelor.bookstatistic.commonslib.dto.request.account;

import java.time.LocalDate;

public record RegisterDto(
    String username,
    String password,
    String confirmPassword,
    String email,
    Boolean enableEmail,
    String telegram,
    LocalDate birthDay,
    String language
) {
}
