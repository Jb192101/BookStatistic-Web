package org.jedi_bachelor.bookstatistic.commonslib.dto.request.account;

import jakarta.validation.constraints.NotNull;

public record LoginDto(
        @NotNull
        String username,

        @NotNull
        String password
) {
}
