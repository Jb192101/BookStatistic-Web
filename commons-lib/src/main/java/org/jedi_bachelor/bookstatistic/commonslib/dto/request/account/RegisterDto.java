package org.jedi_bachelor.bookstatistic.commonslib.dto.request.account;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record RegisterDto(
    String username,
    String password,
    String confirmPassword,
    String email,
    Boolean enableEmail,
    String telegram,

    @JsonFormat(pattern = "dd-MM-yyyy")
    LocalDate birthDay,

    String language
) {
}
