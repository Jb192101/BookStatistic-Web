package org.jedi_bachelor.bookstatistic.commonslib.dto.request.account;

public record RegisterDto(
    String username,
    String password,
    String confirmPassword,
    String email,
    Boolean enableEmail,
    String telegram
) {
}
