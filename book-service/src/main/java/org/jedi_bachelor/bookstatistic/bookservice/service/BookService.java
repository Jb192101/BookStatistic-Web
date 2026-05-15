package org.jedi_bachelor.bookstatistic.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.converter.BookConverter;
import org.jedi_bachelor.bookstatistic.bookservice.converter.TextEntityConverter;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.bookservice.entity.BookTextRelation;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.BookMapper;
import org.jedi_bachelor.bookstatistic.bookservice.redis.RedisContentManager;
import org.jedi_bachelor.bookstatistic.bookservice.redis.entity.TextFile;
import org.jedi_bachelor.bookstatistic.bookservice.repository.*;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.BookNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private final RedisContentManager redisContentManager;

    private final TextEntityConverter textEntityConverter;

    // Пример использования: this.messageSource.getMessage(*код сообщения*);
    private final MessageSource messageSource;

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

    /**
     * Метод связки текста с книгой
     *
     * @param bookId ID книги
     * @param file файл с текстом
     * @throws BookNotFoundException если книги с таким ID не существует
     */
    public void linkTextToBook(UUID bookId, MultipartFile file) throws BookNotFoundException, IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Файл пустой");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("text/plain") && !contentType.equals("text/plain;charset=UTF-8"))) {
            throw new RuntimeException("Поддерживаются только текстовые файлы");
        }

        this.redisContentManager.saveTextFile(bookId, file);

        Optional<Book> bookOptional = this.bookRepository.findById(bookId);

        if(bookOptional.isEmpty()) {
            throw new BookNotFoundException(bookId);
        }

        BookTextRelation relation = new BookTextRelation();
        relation.setBook(bookOptional.get());
        relation.setText(this.textEntityConverter.convert(
                this.redisContentManager.getTextFile(bookId)
        ));

        this.bookTextRepository.save(relation);
    }

    /**
     * Метод выдачи текста книги по ID книги
     *
     * @param bookId ID книги
     * @return файл текста
     * @throws BookNotFoundException если книги с таким ID не существует
     */
    public TextFile getBookTextById(UUID bookId) throws BookNotFoundException {
        if(!this.redisContentManager.exists(bookId.toString())) {
            throw new BookNotFoundException(bookId);
        }

        return this.redisContentManager.getTextFile(bookId);
    }
}
