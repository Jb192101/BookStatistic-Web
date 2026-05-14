package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(UUID id) {
        super(id);
    }
}
