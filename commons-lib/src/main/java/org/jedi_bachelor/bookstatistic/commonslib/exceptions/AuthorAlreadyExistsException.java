package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthorAlreadyExistsException extends Exception {
    private final String firstName;

    private final String middleName;

    private final String lastName;
}
