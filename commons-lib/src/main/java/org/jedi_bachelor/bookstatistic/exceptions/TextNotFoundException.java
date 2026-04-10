package org.jedi_bachelor.bookstatistic.exceptions;

import java.util.UUID;

public class TextNotFoundException extends NotFoundException {
    public TextNotFoundException(UUID id) {
        super(id);
    }
}
