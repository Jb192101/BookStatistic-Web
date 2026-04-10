package org.jedi_bachelor.bookstatistic.exceptions;

import java.util.UUID;

public class NotificationNotFoundException extends NotFoundException {
    public NotificationNotFoundException(UUID id) {
        super(id);
    }
}
