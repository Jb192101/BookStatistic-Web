package org.jedi_bachelor.bookstatistic.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.dto.mapentities.TextDto;
import org.jedi_bachelor.bookstatistic.dto.request.book.BookCreationDto;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.jedi_bachelor.bookstatistic.exceptions.BookNotFoundException;
import org.jedi_bachelor.bookstatistic.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<?> addBookWithoutText(@RequestBody BookCreationDto dto) {
        BookDto bookDto = this.bookService.addBookWithoutText(dto);

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
                new SuccessResponse(201, bookDto)
        );
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<?> linkTextToBook(@PathVariable UUID bookId, @RequestBody TextDto dto) {
        this.bookService.linkTextToBook(bookId, dto);

        return ResponseEntity.ok(
                new SuccessResponse(200, Map.of("linked", true)));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookByBookId(@PathVariable UUID bookId) throws BookNotFoundException {
        BookDto bookDto = this.bookService.getBookById(bookId);

        return ResponseEntity.ok(new SuccessResponse(200, bookDto));
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        List<BookDto> bookDtoList = this.bookService.getAllBooks();

        return ResponseEntity.ok(new SuccessResponse(200, bookDtoList));
    }

    @GetMapping("/{bookId}/text")
    public ResponseEntity<?> getBookText(@PathVariable UUID bookId) {
        return null;
    }
}
