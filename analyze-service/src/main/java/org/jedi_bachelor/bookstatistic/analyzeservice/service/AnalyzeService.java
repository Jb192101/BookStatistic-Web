package org.jedi_bachelor.bookstatistic.analyzeservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.configuration.InteractionPathsConfiguration;
import org.jedi_bachelor.bookstatistic.analyzeservice.entity.BookAnalyzeResult;
import org.jedi_bachelor.bookstatistic.analyzeservice.report.UserReportDocument;
import org.jedi_bachelor.bookstatistic.analyzeservice.repository.BookAnalyzeResultRepository;
import org.jedi_bachelor.bookstatistic.analyzeservice.repository.UserReportRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.ResponseDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserBookRelationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.UserDto;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzeService {
    private final BookAnalyzeResultRepository bookAnalyzeResultRepository;

    private final InteractionClient bookInteractionClient;

    private final InteractionClient responseInteractionClient;

    private final InteractionClient accountInteractionClient;

    private final InteractionPathsConfiguration interactionPathsConfiguration;
    
    private final ObjectMapper objectMapper;

    private final UserReportRepository userReportRepository;

    @Async("analyzeExecutor")
    public CompletableFuture<List<BookDto>> fetchBooksAsync(UUID userId) {
        return CompletableFuture.completedFuture(
                (List<BookDto>) this.bookInteractionClient.sendRequest(
                        HttpMethod.GET,
                        "/user/" + userId.toString()
                )
        );
    }

    @Async("analyzeExecutor")
    public CompletableFuture<List<UserBookRelationDto>> fetchUserBookRelationsAsync(UUID userId) {
        return CompletableFuture.completedFuture(
                (List<UserBookRelationDto>) this.bookInteractionClient.sendRequest(
                        HttpMethod.GET,
                        "/reading/" + userId.toString()
                )
        );
    }

    @Async("analyzeExecutor")
    public CompletableFuture<List<ResponseDto>> fetchResponsesAsync(UUID userId) {
        return CompletableFuture.completedFuture(
                (List<ResponseDto>) this.responseInteractionClient.sendRequest(
                        HttpMethod.GET,
                        this.interactionPathsConfiguration.getResponseGetUsersResponsesUri()
                                + userId.toString()
                )
        );
    }

    @Async("analyzeExecutor")
    public CompletableFuture<UserDto> fetchAccountAsync(UUID userId) {
        return CompletableFuture.completedFuture(
                (UserDto) this.accountInteractionClient.sendRequest(
                        HttpMethod.GET,
                        "/" + userId.toString()
                )
        );
    }

    /**
     * Метод для анализа пользователя и составление о нём отчёта
     */
    public void analyzeUser(UUID userId) throws ExecutionException, InterruptedException {
        log.info("Starting analysis for user: {}", userId);

        // 1. Асинхронно получаем все данные
        CompletableFuture<List<BookDto>> booksFuture = this.fetchBooksAsync(userId);
        CompletableFuture<List<UserBookRelationDto>> relationsFuture = this.fetchUserBookRelationsAsync(userId);
        CompletableFuture<UserDto> userFuture = this.fetchAccountAsync(userId);
        CompletableFuture<List<ResponseDto>> responseFuture = this.fetchResponsesAsync(userId);

        CompletableFuture.allOf(booksFuture, relationsFuture, userFuture, responseFuture).join();

        List<BookDto> books = booksFuture.get();
        List<UserBookRelationDto> relations = relationsFuture.get();
        UserDto user = userFuture.get();
        List<ResponseDto> responses = responseFuture.get();

        if (user == null) {
            log.error("User not found: {}", userId);
            return;
        }

        log.info("Fetched {} books, {} relations, {} responses for user: {}",
                books.size(), relations.size(), responses.size(), userId);

        // 2. Создаём маппинг bookId -> readedPages
        Map<UUID, Integer> readedPagesMap = relations.stream()
                .collect(Collectors.toMap(
                        UserBookRelationDto::bookId,
                        UserBookRelationDto::readedPages,
                        (existing, replacement) -> existing
                ));

        // 3. Анализируем книги пользователя
        List<UserReportDocument.BookStats> bookStats = this.analyzeBooks(books, readedPagesMap);

        // 4. Анализируем отзывы пользователя
        List<UserReportDocument.UserResponseStats> responseStats = this.analyzeResponses(responses);

        // 5. Формируем отчёт
        UserReportDocument document = this.formUserReportDocument(
                userId,
                user,
                bookStats,
                responseStats
        );

        // 6. Сохраняем отчёт в БД
        this.userReportRepository.save(document);
        log.info("User analysis completed and saved for user: {}", userId);
    }

    /**
     * Формирование UserReportDocument
     */
    private UserReportDocument formUserReportDocument(
            UUID userId,
            UserDto user,
            List<UserReportDocument.BookStats> bookStats,
            List<UserReportDocument.UserResponseStats> responseStats) {

        UserReportDocument document = new UserReportDocument();
        document.setUserId(userId);
        document.setBirthday(user.birthDay());
        document.setUsername(user.username());
        document.setResidenceRegion(user.residenceCountry());

        document.setBookStatsList(bookStats);
        document.setUserResponseStatsList(responseStats);

        // Анализируем предпочтения пользователя на основе книг и отзывов
        Map<String, Double> genrePreferences = this.calculateGenrePreferences(userId, bookStats, responseStats);
        document.setGenrePreferences(genrePreferences);

        return document;
    }

    /**
     * Расчёт предпочтений жанров пользователя
     */
    private Map<String, Double> calculateGenrePreferences(
            UUID userId,
            List<UserReportDocument.BookStats> bookStats,
            List<UserReportDocument.UserResponseStats> responseStats) {

        Map<String, Double> genrePreferences = new HashMap<>();

        // Получаем все книг из stats
        List<UUID> bookIds = bookStats.stream()
                .map(UserReportDocument.BookStats::getBookId)
                .collect(Collectors.toList());

        if (bookIds.isEmpty()) {
            log.info("No books found for user {}, cannot calculate genre preferences", userId);
            return genrePreferences;
        }

        // Для каждой книги получаем её жанры из сохранённых результатов анализа
        Map<UUID, Map<String, Double>> bookGenres = new HashMap<>();

        for (UUID bookId : bookIds) {
            // Ищем последнюю версию анализа для книги (version 0 — как заглушка, нужно доработать)
            BookAnalyzeResult analyzeResult = this.bookAnalyzeResultRepository
                    .findById(new BookAnalyzeResult.BookAnalyzeResultId(bookId, 0L))
                    .orElse(null);

            if (analyzeResult != null && analyzeResult.getBookAnalyzeData() != null) {
                try {
                    Map<String, Double> genres = this.objectMapper.readValue(
                            analyzeResult.getBookAnalyzeData(),
                            new com.fasterxml.jackson.core.type.TypeReference<Map<String, Double>>() {}
                    );
                    bookGenres.put(bookId, genres);
                } catch (Exception e) {
                    log.warn("Could not parse genres for book: {}", bookId, e);
                }
            }
        }

        // Агрегируем предпочтения
        for (Map.Entry<UUID, Map<String, Double>> entry : bookGenres.entrySet()) {
            Map<String, Double> genres = entry.getValue();
            for (Map.Entry<String, Double> genreEntry : genres.entrySet()) {
                String genre = genreEntry.getKey();
                Double score = genreEntry.getValue();
                genrePreferences.merge(genre, score, Double::sum);
            }
        }

        // Нормализуем
        if (!bookGenres.isEmpty()) {
            int bookCount = bookGenres.size();
            genrePreferences.replaceAll((k, v) -> v / bookCount);
        }

        // Сортируем по убыванию
        return genrePreferences.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Анализ книг пользователя
     */
    private List<UserReportDocument.BookStats> analyzeBooks(List<BookDto> books, Map<UUID, Integer> readedPagesMap) {
        List<UserReportDocument.BookStats> bookStats = new ArrayList<>();

        for (BookDto book : books) {
            UserReportDocument.BookStats stat = new UserReportDocument.BookStats();
            stat.setBookId(book.id());
            stat.setBookName(book.title());

            // Получаем количество прочитанных страниц из маппинга
            Integer readedPages = readedPagesMap.getOrDefault(book.id(), 0);
            Integer totalPages = book.pages();

            // Вычисляем процент прочитанного
            if (totalPages != null && totalPages > 0) {
                stat.setReadedPercent((int) ((readedPages * 100.0) / totalPages));
            } else {
                stat.setReadedPercent(0);
            }

            // Получаем результаты анализа книги (если есть)
            try {
                BookAnalyzeResult analyzeResult = this.bookAnalyzeResultRepository
                        .findById(new BookAnalyzeResult.BookAnalyzeResultId(book.id(), 0L))
                        .orElse(null);

                if (analyzeResult != null && analyzeResult.getBookAnalyzeData() != null) {
                    // Здесь можно добавить дополнительную информацию из анализа, если нужно
                }
            } catch (Exception e) {
                log.warn("Could not get analyze result for book: {}", book.id(), e);
            }

            bookStats.add(stat);
        }

        return bookStats;
    }

    /**
     * Анализ отзывов пользователя
     */
    private List<UserReportDocument.UserResponseStats> analyzeResponses(List<ResponseDto> responses) {
        return responses.stream()
                .map(response -> {
                    UserReportDocument.UserResponseStats stat = new UserReportDocument.UserResponseStats();
                    stat.setBookId(response.bookId());
                    stat.setResponseText(response.responseText());
                    stat.setStarsCount(response.stars());
                    return stat;
                })
                .collect(Collectors.toList());
    }
}