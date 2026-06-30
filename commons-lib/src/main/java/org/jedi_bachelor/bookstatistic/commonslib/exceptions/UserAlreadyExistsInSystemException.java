package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserAlreadyExistsInSystemException extends Exception {
    private final String username;
}
