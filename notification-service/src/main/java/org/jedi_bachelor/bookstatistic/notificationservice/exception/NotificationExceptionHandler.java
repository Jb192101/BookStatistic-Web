package org.jedi_bachelor.bookstatistic.notificationservice.exception;

import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.NotificationSettingsNotExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserHaventAccessException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;

@RestControllerAdvice
public class NotificationExceptionHandler {
    @ExceptionHandler(NotificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotificationNotFoundException(NotificationNotFoundException e) {
        return new ErrorResponse(
                "Notification not found",
                "Notification not found by notificationId: " + e.getId(),
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(NotificationSettingsNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotiicationSettingsNotFoundException(NotificationSettingsNotExistsException e) {
        return new ErrorResponse(
                "Notification settings not found",
                "Notification settings not found by id: " + e.getId(),
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(
                "User not found",
                "User not found by userId: " + e.getId(),
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(UserHaventAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserHaventAccessException(UserHaventAccessException e) {
        return new ErrorResponse(
                "User haven't access to resource",
                "User with ID: " + e.getCurrentUserId()
                        + " haven't access to resource of user with ID: "
                        + e.getRequiredUserId(),
                HttpStatus.NO_CONTENT.value(),
                Timestamp.from(Instant.now())
        );
    }
}
