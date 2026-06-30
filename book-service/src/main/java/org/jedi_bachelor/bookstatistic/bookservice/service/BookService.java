package org.jedi_bachelor.bookstatistic.bookservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jedi_bachelor.bookstatistic.bookservice.converter.BookConverter;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Author;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Book;
import org.jedi_bachelor.bookstatistic.bookservice.entity.UserBookRelation;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.BookFileStorageService;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.BookAuthorRelationMapper;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.BookMapper;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.UserBookRelationMapper;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.OutboxContentManager;
import org.jedi_bachelor.bookstatistic.bookservice.outbox.entity.OutboxAnalyzeEntity;
import org.jedi_bachelor.bookstatistic.bookservice.repository.AuthorRepository;
import org.jedi_bachelor.bookstatistic.bookservice.repository.BookAuthorRepository;
import org.jedi_bachelor.bookstatistic.bookservice.repository.BookRepository;
import org.jedi_bachelor.bookstatistic.bookservice.repository.UserBookRelationRepository;
import org.jedi_bachelor.bookstatistic.bookservice.utils.BookPagesCalculator;
import org.jedi_bachelor.bookstatistic.commonslib.dto.kafka.KafkaTextAnalyzeDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAuthorRelationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserBookRelationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookCreationUpdatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookIdEntity;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.UserBookRelationCreatingUpdatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.response.book.UserReadingStat;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.*;
import org.jedi_bachelor.bookstatistic.bookservice.entity.BookAuthorRelation;
import org.jedi_bachelor.ioboxstarter.annotation.Outbox;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    private final BookConverter bookConverter;

    private final BookMapper bookMapper;

    private final BookFileStorageService bookFileStorageService;

    private final UserBookRelationRepository userBookRelationRepository;

    private final BookAuthorRepository bookAuthorRepository;

    private final BookAuthorRelationMapper bookAuthorMapper;

    private final BookPagesCalculator bookPagesCalculator;

    private final UserBookRelationMapper userBookMapper;

    private final OutboxContentManager outboxContentManager;

    private static final double SIMILARITY_THRESHOLD = 0.85;

    /**
     * Метод добавления новой книги без текста
     *
     * @param dto DTO на создание книги
     * @return созданную книгу
     */
    @Transactional
    public BookDto addBookWithoutText(BookCreationUpdatingDto dto) {
        Book newBook = this.bookConverter.convert(dto);

        Book savedBook = this.bookRepository.save(newBook);

        return this.bookMapper.toDto(savedBook);
    }

    /**
     * Метод получения всех книг, взятых пользователем себе в библиотеку
     *
     * @param userId ID пользователя
     * @return список книг
     */
    @Transactional
    public List<BookDto> getBooksOfUser(UUID userId) {
        List<Book> resultList = new ArrayList<>();

        List<UUID> ids =
                this.userBookRelationRepository.findById_UserId(userId)
                        .stream()
                        .map(e -> e.getBookId())
                        .toList();

        for(UUID id : ids) {
            Book book = this.bookRepository.findById(id).get();

            resultList.add(book);
        }

        return this.bookMapper.toDtoList(resultList);
    }

    /**
     * Метод возврата статистики книг по пользователю
     *
     * @param userId ID пользователя
     * @return статистику по кол-ву книг каждой категории
     * @throws UserNotFoundException если пользователя с таким ID нет в системе
     */
    public UserReadingStat getReadingStatsByUserId(UUID userId) {
        List<UserBookRelation> userBookRelations = this.userBookRelationRepository.findById_UserId(userId);

        if(userBookRelations.isEmpty()) {
            return new UserReadingStat(
                    userId,
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }

        int allAmount = this.userBookRelationRepository.findById_UserId(userId).size();
        int fullReadedBooksCount = 0;
        int partialReadedBookCount = 0;
        int abandonedBooksCount = 0;
        int gotAndNotReadedBooksCount = 0;

        for(UserBookRelation relation : userBookRelations) {
            Optional<Book> bookOptional = this.bookRepository.findById(relation.getBookId());

            if(bookOptional.isEmpty()) {
                continue;
            }

            Book book = bookOptional.get();

            // 1. Блок с вычислением числа полностью прочитанных страниц
            if(Objects.equals(book.getPages(), relation.getReadedPages())) {
                fullReadedBooksCount++;
                continue;
            }

            // 2. Блок с числом взятых но не открытых книг
            long daysBetween = this.daysBetweenNowAndData(relation.getLastOpeningBookTime());

            if(relation.getReadedPages() == 0 && daysBetween > 31) {
                gotAndNotReadedBooksCount++;
                continue;
            }

            // 3. Блок с вычислением числа частично прочитанными книгами
            if(book.getPages() > relation.getReadedPages()) {
                partialReadedBookCount++;
                continue;
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
     * Метод обновления данных книги
     *
     * @param bookId ID книги для обновления
     * @param dto DTO с данными на обновление
     */
    @Transactional
    public BookDto updateBookData(UUID bookId, BookCreationUpdatingDto dto) throws BookNotFoundException {
        Optional<Book> book = this.bookRepository.findById(bookId);

        if(book.isEmpty()) {
            throw new BookNotFoundException(bookId);
        }

        Book ableBook = book.get();
        ableBook.setTitle(dto.title());
        ableBook.setDescription(dto.description());

        this.bookRepository.save(ableBook);

        return this.bookMapper.toDto(ableBook);
    }

    /**
     * Метод выдачи книги по ID
     *
     * @param bookId ID книги
     * @return книгу, если она есть
     * @throws BookNotFoundException если книга не найдена
     */
    @Transactional
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
    @Transactional
    public List<BookDto> getAllBooks() {
        return this.bookMapper.toDtoList(this.bookRepository.findAll());
    }

    /**
     * Метод выдачи всех текстов в системе
     *
     * @return список текстов
     */
    @Transactional
    public List<TextFile> findAllTexts() {
        return this.bookFileStorageService.findAll();
    }

    /**
     * Метод выдачи текста книги по ID книги
     */
    @Transactional
    public TextFile getBookTextById(UUID bookId) throws BookNotFoundException, TextNotFoundException {
        if (!this.bookExistsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        Optional<TextFile> textFileOptional
                = this.bookFileStorageService.findTextFileByBookId(bookId);

        if(textFileOptional.isEmpty()) {
            throw new TextNotFoundException(bookId);
        }

        return textFileOptional.get();
    }

    /**
     * Метод удаления книги по ID
     *
     * @param bookId ID книги
     */
    @Transactional
    public void deleteBookById(UUID bookId) throws BookNotFoundException {
        if(!this.bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        // Удаление из репозитория книг
        this.bookRepository.deleteById(bookId);

        // Удаление текста книги
        this.bookFileStorageService.deleteById(bookId);

        // Удаление связей пользователей с книгой
        List< UserBookRelation> relations = this.userBookRelationRepository.findById_BookId(bookId);
        relations.forEach(this.userBookRelationRepository::delete);
    }

    /**
     * Метод обновления текста книги
     *
     * @param bookId ID книги
     * @param file файл с новым текстом
     * @return обновлённый текст в качестве подтверждения операции
     */
    @Transactional
    public TextFile updateBookText(UUID bookId, MultipartFile file) throws BookNotFoundException, IOException {
        if(!this.bookRepository.existsById(bookId)) {
            throw new BookNotFoundException(bookId);
        }

        if (file.isEmpty()) {
            throw new RuntimeException("File " + file.getOriginalFilename() + " is empty");
        }

        // Удаление прошлого текста книги
        this.bookFileStorageService.deleteById(bookId);

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("text/plain") && !contentType.equals("text/plain;charset=UTF-8"))) {
            throw new RuntimeException("Supported only text files");
        }

        // Сохранение файла с текстом
        TextFile textFile = this.bookFileStorageService.saveTextFile(file, bookId);

        // Вычисление нового кол-ва страниц в книге
        int newCountOfPages = this.bookPagesCalculator.calculateMetrics(bookId, textFile.getContent()).pages();

        Book book = this.bookRepository.findById(bookId).get();
        book.setPages(newCountOfPages);

        this.bookRepository.save(book);

        // Отправка сообщения в Kafka (через Outbox)
        OutboxAnalyzeEntity entity = new OutboxAnalyzeEntity();
        entity.setBookId(bookId);
        entity.setContent(textFile.getContent());
        entity.setContentType(textFile.getContentType());
        entity.setSize(textFile.getSize());
        entity.setFilename(textFile.getFilename());

        this.outboxContentManager.save(entity);

        log.info("Text linked to book: {}", bookId);

        return textFile;
    }

    /**
     * Метод связки текста с книгой
     */
    @Transactional
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

        // Вычисление нового кол-ва страниц в книге
        int newCountOfPages = this.bookPagesCalculator.calculateMetrics(bookId, textFile.getContent()).pages();

        Book book = this.bookRepository.findById(bookId).get();
        book.setPages(newCountOfPages);

        this.bookRepository.save(book);

        // Отправка сообщения в Kafka (через Outbox)
        OutboxAnalyzeEntity outboxAnalyzeEntity = new OutboxAnalyzeEntity();
        outboxAnalyzeEntity.setBookId(bookId);
        outboxAnalyzeEntity.setFilename(textFile.getFilename());
        outboxAnalyzeEntity.setContent(textFile.getContent());
        outboxAnalyzeEntity.setContentType(textFile.getContentType());
        outboxAnalyzeEntity.setSize(textFile.getSize());

        this.outboxContentManager.save(outboxAnalyzeEntity);

        log.info("Text linked to book: {}", bookId);
    }

    @Transactional
    public UserBookRelationDto addUserBookRelation(UserBookRelationCreatingUpdatingDto dto) throws BookNotFoundException {
        if(!this.bookRepository.existsById(dto.bookId())) {
            throw new BookNotFoundException(dto.bookId());
        }

        UserBookRelation userBookRelation = new UserBookRelation();
        userBookRelation.setId(new UserBookRelation.UserBookRelationId(dto.userId(), dto.bookId()));
        userBookRelation.setLastOpeningBookTime(LocalDateTime.now());
        userBookRelation.setReadedPages(0);

        this.userBookRelationRepository.save(userBookRelation);

        return this.userBookMapper.toDto(userBookRelation);
    }

    @Transactional
    public UserBookRelationDto updateUserBookRelation(UserBookRelationCreatingUpdatingDto dto) throws
            BookNotFoundException, UserBookRelationNotFoundException {
        if(!this.userBookRelationRepository.existsById(new UserBookRelation.UserBookRelationId(dto.userId(), dto.bookId()))) {
            throw new UserBookRelationNotFoundException(dto.userId(), dto.bookId());
        }

        if(!this.bookRepository.existsById(dto.bookId())) {
            throw new BookNotFoundException(dto.bookId());
        }

        if(this.bookRepository.findById(dto.bookId()).get().getPages() < dto.readedPages()) {
            throw new IllegalArgumentException();
        }

        UserBookRelation userBookRelation = new UserBookRelation();
        userBookRelation.setId(new UserBookRelation.UserBookRelationId(dto.userId(), dto.bookId()));
        userBookRelation.setLastOpeningBookTime(LocalDateTime.now());
        userBookRelation.setReadedPages(dto.readedPages());

        this.userBookRelationRepository.save(userBookRelation);

        return this.userBookMapper.toDto(userBookRelation);
    }

    @Transactional
    public UserBookRelationDto getUserBookRelation(UUID userId, BookIdEntity key) throws UserBookRelationNotFoundException {
        Optional<UserBookRelation> relation = this.userBookRelationRepository.findById(new UserBookRelation.UserBookRelationId(userId, key.bookId()));

        if(relation.isEmpty()) {
            throw new UserBookRelationNotFoundException(userId, key.bookId());
        }

        return this.userBookMapper.toDto(relation.get());
    }

    @Transactional
    public List<UserBookRelationDto> findAllUserBookRelations() {
        List<UserBookRelation> relations = this.userBookRelationRepository.findAll();

        return this.userBookMapper.toDtoList(relations);
    }

    @Transactional
    public UserBookRelationDto deleteUserBookRelation(UUID userId, BookIdEntity key) throws UserBookRelationNotFoundException {
        Optional<UserBookRelation> relation = this.userBookRelationRepository.findById(new UserBookRelation.UserBookRelationId(userId, key.bookId()));

        if(relation.isEmpty()) {
            throw new UserBookRelationNotFoundException(userId, key.bookId());
        }

        this.userBookRelationRepository.delete(relation.get());

        return this.userBookMapper.toDto(relation.get());
    }

    @Transactional
    public List<BookAuthorRelationDto> getBookAuthorRelationOfBook(UUID bookId) {
        List<BookAuthorRelation> relations = this.bookAuthorRepository.findByBookId(bookId);

        return this.bookAuthorMapper.toDtoList(relations);
    }

    @Transactional
    public List<BookDto> searchBooksByInputString(String inputString) {
        String lowerCaseResult = inputString.toLowerCase().trim();

        if (lowerCaseResult.isEmpty()) {
            return Collections.emptyList();
        }

        List<Book> exactMatches = new ArrayList<>();
        List<Book> fuzzyMatches = new ArrayList<>();

        List<Book> allBooks = this.bookRepository.findAll();

        JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
        LevenshteinDistance levenshtein = new LevenshteinDistance();

        for (Book book : allBooks) {
            String bookName = book.getTitle().toLowerCase();
            String bookDescription = book.getDescription() != null ? book.getDescription().toLowerCase() : "";

            // Точное вхождение - приоритет
            if (bookName.contains(lowerCaseResult) || bookDescription.contains(lowerCaseResult)) {
                exactMatches.add(book);
                continue;
            }

            // Поиск по авторам
            List<Author> authors = this.bookAuthorRepository
                    .findByBookId(book.getId())
                    .stream()
                    .map(BookAuthorRelation::getAuthor)
                    .toList();

            boolean authorFound = false;
            for (Author author : authors) {
                String fullName = (author.getFirstName() + " " + author.getLastName()).toLowerCase();
                if (fullName.contains(lowerCaseResult)) {
                    exactMatches.add(book);
                    authorFound = true;
                    break;
                }
            }
            if (authorFound) {
                continue;
            }

            // JaroWinkler - только если длина запроса >= 3
            if (lowerCaseResult.length() >= 3) {
                double similarity = jaroWinkler.apply(lowerCaseResult, bookName);

                double threshold = this.calculateThreshold(lowerCaseResult.length());

                if (similarity >= threshold) {
                    fuzzyMatches.add(book);
                }
            }
        }

        // Сортировка по релевантности (Левенштейн)
        fuzzyMatches.sort((b1, b2) -> {
            int d1 = levenshtein.apply(lowerCaseResult, b1.getTitle().toLowerCase());
            int d2 = levenshtein.apply(lowerCaseResult, b2.getTitle().toLowerCase());
            return Integer.compare(d1, d2);
        });

        // Объединяем результаты: сначала точные, потом нечёткие
        List<Book> allResults = new ArrayList<>();
        allResults.addAll(exactMatches);
        allResults.addAll(fuzzyMatches);

        log.info("Found {} books for query '{}' (exact: {}, fuzzy: {})",
                allResults.size(), inputString, exactMatches.size(), fuzzyMatches.size());

        return this.bookMapper.toDtoList(allResults);
    }

    /**
     * Метод динамического вычисления входного порога для строк для алгоритма Jaro-Winkler
     * Чем длиннее строка - тем порог ниже
     */
    private double calculateThreshold(int len) {
        if (len <= 3) return 0.85;
        if (len <= 5) return 0.75;
        if (len <= 7) return 0.65;
        return 0.55;
    }

    /**
     * Проверка существования книги по ID
     */
    private boolean bookExistsById(UUID bookId) {
        return this.bookRepository.existsById(bookId);
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
}
