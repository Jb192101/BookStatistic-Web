package org.jedi_bachelor.bookstatistic.bookservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.converter.BookConverter;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.bookservice.entity.UserBookRelation;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.BookFileStorageService;
import org.jedi_bachelor.bookstatistic.bookservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.BookMapper;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.bookservice.repository.*;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.book.UserReadingStat;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.BookNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.TextAlreadyLinkedException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.TextNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookAuthorRepository bookAuthorRepository;

    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    private final BookMapper bookMapper;

    private final BookFileStorageService bookFileStorageService;

    private final UserBookRelationRepository userBookRelationRepository;

    private final OutboxContentManager outboxContentManager;

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

        Book savedBook = this.bookRepository.save(newBook);

        return this.bookMapper.toDto(savedBook);
    }

    /**
     * Получение только содержимого текста (без метаданных)
     */
    public String getBookTextContent(UUID bookId) throws BookNotFoundException, TextNotFoundException {
        if (!this.bookExistsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        return this.bookFileStorageService.readFileContent(bookId);
    }

    /**
     * Метод возврата статистики книг по пользователю
     *
     * @param userId ID пользователя
     * @return статистику по кол-ву книг каждой категории
     * @throws UserNotFoundException если пользователя с таким ID нет в системе
     */
    public UserReadingStat getReadingStatsByUserId(UUID userId) throws UserNotFoundException {
        List<UserBookRelation> userBookRelations = this.userBookRelationRepository.findById_UserId(userId);

        if(userBookRelations.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        int allAmount = this.userBookRelationRepository.findById_UserId(userId).size();
        int fullReadedBooksCount = 0;
        int partialReadedBookCount = 0;
        int abandonedBooksCount = 0;
        int gotAndNotReadedBooksCount = 0;

        for(UserBookRelation relation : userBookRelations) {
            // 1. Блок с вычислением числа полностью прочитанных страниц

            // 2. Блок с вычислением числа частично прочитанными книгами

            // 3. Блок с числом взятых но не открытых книг
            long daysBetween = this.daysBetweenNowAndData(relation.getLastOpeningBookTime());

            if(relation.getReadedPages() == 0 && daysBetween > 31) {
                gotAndNotReadedBooksCount++;
            }

            // 4. Блок с заброшенными книгами
            if(daysBetween > 31 && relation.getReadedPages() != 0) {
                abandonedBooksCount++;
            }
        }

        return new UserReadingStat(
                userId,
                allAmount,
                fullReadedBooksCount,
                partialReadedBookCount,
                abandonedBooksCount,
                gotAndNotReadedBooksCount
        );
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
     */
    public void linkTextToBook(UUID bookId, MultipartFile file)
            throws BookNotFoundException, IOException, TextAlreadyLinkedException, TextNotFoundException {

        if (!this.bookExistsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        if (this.bookFileStorageService.exists(bookId)) {
            throw new TextAlreadyLinkedException(bookId);
        }

        if (file.isEmpty()) {
            throw new RuntimeException("File " + file.getOriginalFilename() + " is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("text/plain") && !contentType.equals("text/plain;charset=UTF-8"))) {
            throw new RuntimeException("Supported only text files");
        }

        // Сохранение файла с текстом
        TextFile textFile = this.bookFileStorageService.saveTextFile(file, bookId);

        // Отправка сообщения в Kafka (через Outbox)
        KafkaTextAnalyzeDto dto = new KafkaTextAnalyzeDto(
                bookId,
                textFile.getFilename(),
                textFile.getContent(),
                textFile.getContentType(),
                textFile.getSize(),
                textFile.getUploadTime()
        );

        this.outboxContentManager.save(dto);

        log.info("Text linked to book: {}", bookId);
    }

    /**
     * Метод выдачи текста книги по ID книги
     */
    public TextFile getBookTextById(UUID bookId) throws BookNotFoundException, TextNotFoundException {
        if (!this.bookExistsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        return this.bookFileStorageService.findTextFileByBookId(bookId);
    }

    /**
     * Удаление текста книги
     */
    @Transactional
    public void deleteBookText(UUID bookId) throws BookNotFoundException, TextNotFoundException {
        if (!this.bookExistsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        // Удаляем файл
        boolean deleted = this.bookFileStorageService.deleteByBookId(bookId);

        if (deleted) {
            log.info("Text deleted for book: {}", bookId);
        } else {
            throw new TextNotFoundException(bookId);
        }
    }

    /**
     * Метод вычисления разницы между нынешней датой и введённой датой в днях
     *
     * @param date дата
     * @return разницу в днях
     */
    private long daysBetweenNowAndData(LocalDateTime date) {
        return ChronoUnit.DAYS.between(date, LocalDateTime.now());
    }

    /**
     * Метод для подсчёта числа страниц на основе числа символов в тексте
     * (при подсчёте числа страниц используется метрика 1700)
     *
     * @param text сущность текста
     * @return кол-во символов
     */
    private int getCountOfPagesBySymbols(TextFile text) {
        return (int) text.getSize() / 1700;
    }

    /**
     * Проверка существования книги по ID
     */
    private boolean bookExistsById(UUID bookId) {
        return this.bookRepository.existsById(bookId);
    }

    /**
     * Конвертация File в TextFile (если нужен этот метод)
     */
    private TextFile convertFileToTextFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }

        Path path = file.toPath();
        String content = Files.readString(path, StandardCharsets.UTF_8);
        String fileName = path.getFileName().toString();

        return TextFile.builder()
                .filename(fileName)
                .content(content)
                .contentType("text/plain")
                .size(Files.size(path))
                .uploadTime(LocalDateTime.now())
                .build();
    }
}
