package org.jedi_bachelor.bookstatistic.bookservice.exception;

import org.jedi_bachelor.bookstatistic.commonslib.dto.response.ErrorResponse;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;

@RestControllerAdvice
public class BookExceptionHandler {
    @ExceptionHandler(AuthorAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAuthorAlreadyExistsException(AuthorAlreadyExistsException e) {
        return new ErrorResponse(
                "Author already exists",
                "Author with name " + e.getFirstName() + " " + e.getMiddleName() + " " + e.getLastName() + " already exists",
                HttpStatus.CONFLICT.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAuthorNotFoundException(AuthorNotFoundException e) {
        return new ErrorResponse(
                "Author not found",
                "Author with id " + e.getId() + " not found",
                HttpStatus.CONFLICT.value(),
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

    @ExceptionHandler(BookAuthorRelationAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookAuthorRelationAlreadyExistsException(BookAuthorRelationAlreadyExistsException e) {
        return new ErrorResponse(
                "Book Author Relation not found",
                "Book Author Relation not found by bookId " + e.getBookId() + " and authorId " + e.getAuthorId(),
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(ResponseAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleResponseAlreadyExistsException(ResponseAlreadyExistsException e) {
        return new ErrorResponse(
                "Response already exists",
                "Response already exists with bookId " + e.getBookId(),
                HttpStatus.CONFLICT.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(TextAlreadyLinkedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleTextnAlreadyLinkedException(TextAlreadyLinkedException e) {
        return new ErrorResponse(
                "Text already linked",
                "Text already linked to book with ID " + e.getBookId(),
                HttpStatus.CONFLICT.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(TextNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTextNotFoundException(TextNotFoundException e) {
        return new ErrorResponse(
                "Text not found",
                "Text with id " + e.getId() + " not found",
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(UserBookRelationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserBookRelationNotFoundException(UserBookRelationNotFoundException e) {
        return new ErrorResponse(
                "User Book Relation not found",
                "User Book Relation with userId " + e.getUserId() + " and bookId " + e.getBookId() + " not found",
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }

    @ExceptionHandler(ResponseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResponseNotFoundException(ResponseNotFoundException e) {
        return new ErrorResponse(
                "Response not found",
                "Response with userId " + e.getUserId() + " and bookId " + e.getBookId() + " not found",
                HttpStatus.NOT_FOUND.value(),
                Timestamp.from(Instant.now())
        );
    }
}
