package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.dto.UpdateBookDto;
import org.jedi_bachelor.model.entities.Book;
import org.jedi_bachelor.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    @Autowired
    private final BookRepository bookRepository;

    public void addNewBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(UpdateBookDto dto) {
        Book book = bookRepository.getReferenceById(dto.getBookId());

        book.setReadedPages(book.getReadedPages() + dto.getNewReadedPages());

        bookRepository.save(book);
    }
}
