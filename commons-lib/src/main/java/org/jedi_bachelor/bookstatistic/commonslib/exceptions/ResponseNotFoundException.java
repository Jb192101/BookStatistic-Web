package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ResponseNotFoundException extends Exception {
    private final UUID userId;

    private final UUID bookId;
}
