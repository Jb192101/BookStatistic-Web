package org.jedi_bachelor.bookstatistic.commonslib.exceptions;

import java.util.UUID;

public class NotificationSettingsNotExistsException extends NotFoundException {
    public NotificationSettingsNotExistsException(UUID id) {
        super(id);
    }
}
