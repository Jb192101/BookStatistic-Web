package org.jedi_bachelor.bookstatistic.commonslib.dto.request.account;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record UserUpdateDto(
        String username,
        String password,
        String confirmPassword,
        String firstName,
        String middleName,
        String lastName,
        String language,
        String email,

        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate birthDay
) {
}
