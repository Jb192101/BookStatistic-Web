package org.jedi_bachelor.bookstatistic.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.converter.BookConverter;
import org.jedi_bachelor.bookstatistic.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.dto.mapentities.TextDto;
import org.jedi_bachelor.bookstatistic.dto.request.book.BookCreationDto;
import org.jedi_bachelor.bookstatistic.entity.Book;
import org.jedi_bachelor.bookstatistic.entity.BookTextRelation;
import org.jedi_bachelor.bookstatistic.entity.Text;
import org.jedi_bachelor.bookstatistic.exceptions.BookNotFoundException;
import org.jedi_bachelor.bookstatistic.mapper.BookMapper;
import org.jedi_bachelor.bookstatistic.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookTextRepository bookTextRepository;

    private final BookAuthorRepository bookAuthorRepository;

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final TextRepository textRepository;

    private final BookConverter bookConverter;

    private final BookMapper bookMapper;

    /**
     * Метод добавления новой книги без текста
     *
     * @param dto DTO на создание книги
     * @return созданную книгу
     */
    public BookDto addBookWithoutText(BookCreationDto dto) {
        Book newBook = this.bookConverter.convert(dto);

        this.bookRepository.save(newBook);

        return this.bookMapper.toDto(newBook);
    }

    /**
     * Метод выдачи книги по ID
     *
     * @param bookId ID книги
     * @return книгу, если она есть
     * @throws BookNotFoundException если книга не найдена
     */
    public BookDto getBookById(UUID bookId) throws BookNotFoundException {
        Optional<Book> bookOptional = this.bookRepository.findById(bookId);

        if(bookOptional.isEmpty()) {
            throw new BookNotFoundException(bookId);
        }

        return this.bookMapper.toDto(bookOptional.get());
    }

    /**
     * Метод выдачи всех книг в системе
     *
     * @return список книг
     */
    public List<BookDto> getAllBooks() {
        return this.bookMapper.toDtoList(this.bookRepository.findAll());
    }

    public void linkTextToBook(UUID bookId, TextDto dto) throws BookNotFoundException {
        Optional<Book> bookOptional = this.bookRepository.findById(bookId);

        if(bookOptional.isEmpty()) {
            throw new BookNotFoundException(bookId);
        }

        BookTextRelation relation = new BookTextRelation();
    }
}
