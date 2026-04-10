package org.jedi_bachelor.bookstatistic.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
abstract public class NotFoundException extends Exception {
    protected String id;

    public NotFoundException(UUID id) {
        this.id = id.toString();
    }
}
