package org.jedi_bachelor.bookstatistic.commonslib.dto.request.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record RegisterDto(
        @NotNull
        String username,

        @NotNull
        String password,

        @NotNull
        String confirmPassword,

        @NotNull
        String firstName,

        String middleName,

        @NotNull
        String lastName,

        @NotNull
        @Email
        String email,

        @NotNull
        Boolean enableEmail,

        @NotNull
        Boolean enableBroadcast,

        @NotNull
        String telegram,

        @JsonFormat(pattern = "dd-MM-yyyy")
        @Past
        LocalDate birthDay,

        @NotNull
        @Max(value = 2)
        String language
) {
}
