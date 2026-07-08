package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class AuthorAlreadyExistsException extends Exception {
    private final String firstName;

    private final String middleName;

    private final String lastName;
}
