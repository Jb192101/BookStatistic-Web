package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PasswordInvalidException extends Exception {
    private final String password;

    private final String confirmPassword;
}
