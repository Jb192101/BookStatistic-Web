package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UsernameAlreadyExistsException extends Exception {
    private final String username;
}
