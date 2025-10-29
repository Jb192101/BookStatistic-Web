package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.dto.UpdateBookDto;
import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.model.enums.GenreType;
import org.jedi_bachelor.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    @Autowired
    private final BookService bookService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void addNewBook(@RequestBody Book book) {
        bookService.addNewBook(book);
    }

    @PatchMapping("/update/{id}")
    public void updateBookById(@PathVariable Long id) {
        bookService.updateBook(id);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = this.bookService.getBookById(id);

        if(book.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(book.get());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            List<Book> books = this.bookService.getAllBooks();

            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/genres/{genre}")
    public ResponseEntity<List<Book>> getBooksOfSuchGenre(@PathVariable GenreType genre) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchGenre(genre);

            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/authors/{author}")
    public ResponseEntity<List<Book>> getBooksOfSuchAuthor(@PathVariable String author) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchAuthor(author);

            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/titles/{title}")
    public ResponseEntity<List<Book>> getBooksOfSuchTitle(@PathVariable String title) {
        try {
            List<Book> books = this.bookService.getBooksOfSuchTitle(title);

            return ResponseEntity.ok(books);
        } catch(Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
