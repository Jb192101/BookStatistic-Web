package org.jedi_bachelor.controller;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.dto.UpdateBookDto;
import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    @Autowired
    private final BookService bookService;

    @PostMapping("/post")
    public void addNewBook(Book book) {
        bookService.addNewBook(book);
    }

    @PatchMapping("/update")
    public void updateBook(UpdateBookDto dto) {
        bookService.updateBook(dto);
    }
}
