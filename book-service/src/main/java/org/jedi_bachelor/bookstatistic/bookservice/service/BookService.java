package org.jedi_bachelor.bookstatistic.bookservice.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.converter.BookConverter;
import org.jedi_bachelor.bookstatistic.bookservice.converter.TextEntityConverter;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Text;
import org.jedi_bachelor.bookstatistic.bookservice.entity.UserBookRelation;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.BookFileStorageService;
import org.jedi_bachelor.bookstatistic.bookservice.kafka.KafkaProducer;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.BookMapper;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.jedi_bachelor.bookstatistic.bookservice.repository.*;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookCreationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.book.UserReadingStat;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.BookNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.TextAlreadyLinkedException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.UserNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookAuthorRepository bookAuthorRepository;

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final TextRepository textRepository;

    private final BookConverter bookConverter;

    private final BookMapper bookMapper;

    private final BookFileStorageService bookFileStorageService;

    private final TextEntityConverter textEntityConverter;

    private final UserBookRelationRepository userBookRelationRepository;

    private final KafkaProducer kafkaProducer;

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
     *
     * @param bookId ID книги
     * @param file файл с текстом
     * @throws BookNotFoundException если книги с таким ID не существует
     */
    public void linkTextToBook(UUID bookId, MultipartFile file) throws BookNotFoundException, IOException, TextAlreadyLinkedException {
        if(!this.bookExistsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        if(this.textAlreadyLinkedToBook(bookId)) {
            throw new TextAlreadyLinkedException(bookId);
        }

        if (file.isEmpty()) {
            throw new RuntimeException("Файл пустой");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("text/plain") && !contentType.equals("text/plain;charset=UTF-8"))) {
            throw new RuntimeException("Поддерживаются только текстовые файлы");
        }

        // Сохранение файла с текстом
        //String fileKey = this.redisContentManager.saveTextFile(bookId, file);

        // Отправка сообщения в топик
        //TextFile textFile = this.redisContentManager.getTextFile(fileKey);

        /*
        KafkaTextAnalyzeDto dto = new KafkaTextAnalyzeDto(
                bookId,
                textFile.getFilename(),
                textFile.getContent(),
                textFile.getContentType(),
                textFile.getSize(),
                textFile.getUploadTime()
        );
         */

        // Добавление сообщения в outbox
        this.kafkaProducer.sendMessageToBookTextAnalyzeTopic(
                dto
        );
    }

    /**
     * Метод выдачи текста книги по ID книги
     *
     * @param bookId ID книги
     * @return файл текста
     * @throws BookNotFoundException если книги с таким ID не существует
     */
    public TextFile getBookTextById(UUID bookId) throws BookNotFoundException {
        //if(!this.redisContentManager.exists(bookId.toString())) {
        //    throw new BookNotFoundException(bookId);
        //}

        //return this.redisContentManager.getTextFile(bookId);
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
     *
     * @param bookId ID книги
     * @return true, если книга существует
     */
    private boolean bookExistsById(UUID bookId) {
        Optional<Book> book = this.bookRepository.findById(bookId);

        return book.isPresent();
    }

    /**
     * Проверка того, привязан ли уже к этой книге текст или нет
     *
     * @param bookId ID книги
     * @return true, если уже привязан
     */
    private boolean textAlreadyLinkedToBook(UUID bookId) {
        Optional<Text> text = this.textRepository.findByBookId(bookId);

        return text.isPresent();
    }
}
