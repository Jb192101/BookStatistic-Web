package org.jedi_bachelor.bookstatistic.accountservice.exception;

import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.PasswordInvalidException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserAlreadyExistsInSystemException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;

@RestControllerAdvice
public class AccountExceptionHandler {
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
}
