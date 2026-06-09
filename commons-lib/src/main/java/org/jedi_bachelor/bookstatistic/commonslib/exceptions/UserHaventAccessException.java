package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UserHaventAccessException extends Exception {
    private final UUID currentUserId;
    private final UUID requiredUserId;
}