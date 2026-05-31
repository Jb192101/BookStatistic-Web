package org.jedi_bachelor.bookstatistic.notificationservice.inbox;

/**
 * Типы поступающих операций из outbox в других сервисах
 */

public enum InboxOperation {
    DELETE_OPERATION,
    ADD_OPERATION,
    UPDATE_OPERATION
}
