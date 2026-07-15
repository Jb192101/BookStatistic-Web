package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import java.util.UUID;

public class AuthorNotFoundException extends NotFoundException {
    public AuthorNotFoundException(UUID id) {
        super(id);
    }
}
