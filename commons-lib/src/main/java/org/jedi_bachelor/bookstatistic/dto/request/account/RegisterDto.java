package org.jedi_bachelor.bookstatistic.dto.request.account;

public record RegisterDto(
    String username,
    String password,
    String confirmPassword,
    String email,
    Boolean enableEmail,
    String telegram
) {
}
