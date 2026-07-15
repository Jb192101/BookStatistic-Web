package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import java.util.UUID;

public class NotificationNotFoundException extends NotFoundException {
    public NotificationNotFoundException(UUID id) {
        super(id);
    }
}
