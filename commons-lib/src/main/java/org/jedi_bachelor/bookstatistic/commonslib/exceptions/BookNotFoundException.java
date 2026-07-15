package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BookNotFoundException extends NotFoundException {
    public BookNotFoundException(UUID id) {
        super(id);
    }
}
