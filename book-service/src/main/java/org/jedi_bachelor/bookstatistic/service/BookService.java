package org.jedi_bachelor.bookstatistic.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.converter.BookConverter;
import org.jedi_bachelor.bookstatistic.repository.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookTextRepository bookTextRepository;

    private final BookAuthorRepository bookAuthorRepository;

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final TextRepository textRepository;

    private final BookConverter bookConverter;
}
