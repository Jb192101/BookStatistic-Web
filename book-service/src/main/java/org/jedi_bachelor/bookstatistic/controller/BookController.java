package org.jedi_bachelor.bookstatistic.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.dto.mapentities.TextDto;
import org.jedi_bachelor.bookstatistic.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<?> addBookWithoutText() {
        return null;
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<?> linkTextToBook(@PathVariable UUID bookId, @RequestBody TextDto dto) {
        return null;
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookByBookId(@PathVariable UUID bookId) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        return null;
    }

    @GetMapping("/{bookId}/text")
    public ResponseEntity<?> getBookText(@PathVariable UUID bookId) {
        return null;
    }
}
