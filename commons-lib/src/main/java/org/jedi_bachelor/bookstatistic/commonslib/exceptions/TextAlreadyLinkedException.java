package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TextAlreadyLinkedException extends Exception {
    private final String bookId;

    public TextAlreadyLinkedException(UUID bookId) {
        this.bookId = bookId.toString();
    }
}
