package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class BookAuthorRelationNotFoundException extends Exception {
    private final UUID bookId;

    private final UUID authorId;
}
