package org.jedi_bachelor.bookstatistic.commonslib.exceptions.handler;

import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@Deprecated
@RestControllerAdvice
public class GlobalExceptionHandler {
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

    @ExceptionHandler(UserAlreadyExistsInSystemException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotificationNotFoundException(UserAlreadyExistsInSystemException e) {
        return new ErrorResponse(
                "User already exists",
                "User with username already exists: " + e.getUsername(),
                HttpStatus.CONFLICT.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(PasswordInvalidException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotificationNotFoundException(PasswordInvalidException e) {
        return new ErrorResponse(
                "User already exists",
                "Password invalid: " + e.getPassword() + ", " + e.getConfirmPassword(),
                HttpStatus.CONFLICT.value(),
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

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.LENGTH_REQUIRED) // ПОТОМ ПОМЕНЯТЬ
    public ErrorResponse handleIOException(IOException e) {
        return new ErrorResponse(
                "IOException",
                e.getMessage(),
                HttpStatus.LENGTH_REQUIRED.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.LENGTH_REQUIRED) // ПОТОМ ПОМЕНЯТЬ
    public ErrorResponse handleRuntimeException(RuntimeException e) {
        return new ErrorResponse(
                "Ошибка во время исполнения",
                e.getMessage(),
                HttpStatus.LENGTH_REQUIRED.value(),
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

    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookNotFoundException(BookNotFoundException e) {
        return new ErrorResponse(
                "Book not found",
                "Book not found by bookId: " + e.getId(),
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(TextNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTextNotFoundException(TextNotFoundException e) {
        return new ErrorResponse(
                "Text not found",
                "Text not found by textId: " + e.getId(),
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }
}
