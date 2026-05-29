package org.jedi_bachelor.bookstatistic.analyzeservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.analyzeservice.entity.BookAnalysis;
import org.jedi_bachelor.bookstatistic.analyzeservice.repository.BookAnalysisRepository;
import org.jedi_bachelor.bookstatistic.analyzeservice.service.ml.GenreClassifier;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAnalysisResponse;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookDto;
import org.jedi_bachelor.bookstatistic.commonslib.internalinteraction.InteractionClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyzeService {
    private final BookAnalysisRepository bookAnalysisRepository;

    private final InteractionClient bookInteractionClient;

    private final InteractionClient accountInteractionClient;

    private final GenreClassifier genreClassifier;

    private final ObjectMapper objectMapper;

    private static final String MODEL_VERSION = "sbert-custom-v1.0";
    private static final double SIGNIFICANCE_THRESHOLD = 0.5;
    private static final int TOP_GENRES_LIMIT = 3;

    /**
     * Публичный метод анализа пользователя
     */
    public List<BookAnalysisResponse> analyzeUser(UUID userId) {
        log.info("Starting AI-powered analysis for user: {}", userId);
        return analyze(userId);
    }

    /**
     * Анализ группы случайных пользователей (для админа)
     */
    public List<BookAnalysisResponse> analyzeUserGroup(int countOfUsers) {
        log.info("Starting batch analysis for {} random users", countOfUsers);

        List<UUID> randomUserIds = (List<UUID>) this.accountInteractionClient
                .sendRequest(HttpMethod.GET, "/random?count=" + countOfUsers)
                .getBody();

        List<BookAnalysisResponse> allResults = new ArrayList<>();

        for (UUID userId : randomUserIds) {
            try {
                List<BookAnalysisResponse> userResults = analyzeUser(userId);
                allResults.addAll(userResults);
            } catch (Exception e) {
                log.error("Failed to analyze user: {}", userId, e);
            }
        }

        return allResults;
    }

    /**
     * Основной метод анализа пользователя по книгам
     *
     * @param userId ID пользователя
     * @return список результата анализа
     */
    @CircuitBreaker(
            name = "bookService",
            fallbackMethod = "fallbackForBookService"
    )
    @Retry(
            name = "bookService",
            fallbackMethod = "fallbackForBookService"
    )
    @Transactional
    private List<BookAnalysisResponse> analyze(UUID userId) {
        log.info("Starting analysis pipeline for user: {}", userId);

        // 1. Получение всех книг пользователя
        List<BookDto> userBooks = this.fetchUserBooks(userId);

        if (userBooks.isEmpty()) {
            log.info("No books found for user: {}", userId);
            return Collections.emptyList();
        }

        // 2. Определяем, какие книги нужно анализировать (только новые)
        List<BookDto> booksToAnalyze = this.filterNewBooks(userId, userBooks);

        if (booksToAnalyze.isEmpty()) {
            log.info("All books already analyzed for user: {}", userId);
            return this.getExistingAnalyses(userId);
        }

        log.info("Found {} new books to analyze for user: {}",
                booksToAnalyze.size(), userId);

        // 3. AI-анализ каждой новой книги
        List<BookAnalysis> newAnalyses = new ArrayList<>();

        for (BookDto book : booksToAnalyze) {
            try {
                BookAnalysis analysis = this.analyzeBookWithAI(book);
                newAnalyses.add(analysis);

                log.info("AI analysis completed for book: {} - {}",
                        book.id(), book.title());

            } catch (Exception e) {
                log.error("Failed to analyze book: {}", book.id(), e);
            }
        }

        // 4. Сохраняем все новые результаты
        if (!newAnalyses.isEmpty()) {
            this.saveBooksAnalysis(newAnalyses);
        }

        // 5. Возвращаем все анализы (старые + новые)
        return this.getAllAnalysesForUser(userId);
    }

    /**
     * Сохранение результатов анализа
     */
    private void saveBooksAnalysis(List<BookAnalysis> analyses) {
        bookAnalysisRepository.saveAll(analyses);
        log.info("Saved {} analysis results", analyses.size());
    }

    /**
     * AI-анализ одной книги
     */
    private BookAnalysis analyzeBookWithAI(BookDto book) {
        log.debug("Running AI pipeline for book: {}", book.id());

        // 1. Получаем текст книги из book-service
        String bookText = this.fetchBookText(book.id());

        if (bookText == null || bookText.trim().isEmpty()) {
            throw new RuntimeException("No text content available for book: " + book.id());
        }

        // 2. Подготавливаем текст для анализа
        String preparedText = this.prepareTextForAnalysis(bookText, book);

        // 3. Запускаем классификатор жанров (DL4J)
        Map<String, Double> genrePredictions = genreClassifier.predict(preparedText);

        // 4. Создаём сущность с результатами
        BookAnalysis analysis = new BookAnalysis();
        analysis.setBookId(book.id());
        analysis.setAnalyzedAt(LocalDateTime.now());
        analysis.setModelVersion(MODEL_VERSION);
        analysis.setConfidenceScore(this.calculateConfidence(genrePredictions));

        // 5. Распределяем предсказания по категориям
        try {
            // Основные жанры
            Map<String, Double> mainGenres = this.extractMainGenres(genrePredictions);
            analysis.setGenres(objectMapper.writeValueAsString(mainGenres));

            // Поджанры
            Map<String, Double> subgenres = this.extractSubgenres(genrePredictions);
            analysis.setSubgenres(objectMapper.writeValueAsString(subgenres));

            // Элементы повествования
            Map<String, Double> narrativeElements = extractNarrativeElements(genrePredictions);
            analysis.setNarrativeElements(objectMapper.writeValueAsString(narrativeElements));

            // Темп
            Map<String, Double> pacing = this.extractPacing(genrePredictions);
            analysis.setPacing(objectMapper.writeValueAsString(pacing));

            // Атмосфера
            Map<String, Double> atmosphere = this.extractAtmosphere(genrePredictions);
            analysis.setAtmosphere(objectMapper.writeValueAsString(atmosphere));

            // Токены модели (для отладки)
            analysis.setTokens(objectMapper.writeValueAsString(genrePredictions));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize analysis results", e);
            throw new RuntimeException("JSON serialization failed", e);
        }

        return analysis;
    }

    /**
     * Подготовка текста для анализа
     */
    private String prepareTextForAnalysis(String bookText, BookDto book) {
        StringBuilder sb = new StringBuilder();

        // Добавляем метаданные для контекста
        if (book.title() != null) {
            sb.append("Title: ").append(book.title()).append(". ");
        }
        if (book.description() != null) {
            sb.append("Description: ").append(book.description()).append(". ");
        }

        // Добавляем основной текст (обрезаем до ~500 токенов)
        String cleanText = bookText.replaceAll("\\s+", " ").trim();
        if (cleanText.length() > 2000) {
            // Берем начало и конец (вдруг развязка в конце)
            String beginning = cleanText.substring(0, 1500);
            String ending = cleanText.substring(cleanText.length() - 500);
            cleanText = beginning + " ... " + ending;
        }

        sb.append("Content: ").append(cleanText);

        return sb.toString();
    }

    /**
     * Извлечение основных жанров
     */
    private Map<String, Double> extractMainGenres(Map<String, Double> allPredictions) {
        String[] mainGenres = {
                "fantasy", "science_fiction", "romance", "detective",
                "thriller", "horror", "adventure", "historical",
                "mystery", "drama", "comedy", "tragedy"
        };

        return filterPredictions(allPredictions, mainGenres);
    }

    /**
     * Извлечение поджанров
     */
    private Map<String, Double> extractSubgenres(Map<String, Double> allPredictions) {
        String[] subgenres = {
                "dark_fantasy", "urban_fantasy", "epic_fantasy", "heroic_fantasy",
                "cyberpunk", "space_opera", "dystopia", "post_apocalyptic",
                "noir", "cozy_mystery", "police_procedural", "psychological_thriller",
                "romantic_comedy", "dramatic_romance", "paranormal_romance",
                "bildungsroman", "plot_twists", "moral_dilemma"
        };

        return filterPredictions(allPredictions, subgenres);
    }

    /**
     * Вычисление общей уверенности модели
     */
    private Float calculateConfidence(Map<String, Double> predictions) {
        if (predictions.isEmpty()) return 0.0f;

        double avgConfidence = predictions.values().stream()
                .filter(v -> v > 0.3) // Учитываем только значимые предсказания
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return (float) (Math.round(avgConfidence * 1000.0) / 1000.0);
    }

    /**
     * Получение текста книги через book-service
     */
    private String fetchBookText(UUID bookId) {
        log.debug("Fetching text for book: {}", bookId);

        try {
            String text = (String) this.bookInteractionClient
                    .sendRequest(HttpMethod.GET, "/texts/" + bookId)
                    .getBody();

            return text;

        } catch (Exception e) {
            log.warn("Failed to fetch text for book: {}. Using description fallback", bookId);
            return null; // Будет использовано описание книги
        }
    }

    /**
     * Извлечение элементов повествования
     */
    private Map<String, Double> extractNarrativeElements(Map<String, Double> allPredictions) {
        String[] narrativeElements = {
                "first_person", "nonlinear_timeline", "unreliable_narrator",
                "multiple_pov", "worldbuilding", "magic_system",
                "philosophical_depth", "social_commentary"
        };

        return filterPredictions(allPredictions, narrativeElements);
    }

    /**
     * Извлечение темпа
     */
    private Map<String, Double> extractPacing(Map<String, Double> allPredictions) {
        String[] pacingElements = {
                "action_scenes", "dialogues", "descriptions",
                "introspective", "overall_pacing"
        };

        return filterPredictions(allPredictions, pacingElements);
    }

    /**
     * Извлечение атмосферы
     */
    private Map<String, Double> extractAtmosphere(Map<String, Double> allPredictions) {
        String[] atmosphereElements = {
                "dark", "tense", "mysterious", "romantic",
                "humorous", "melancholic", "inspiring", "nostalgic"
        };

        return filterPredictions(allPredictions, atmosphereElements);
    }

    /**
     * Фильтрация предсказаний по заданным ключам
     */
    private Map<String, Double> filterPredictions(
            Map<String, Double> allPredictions, String[] keys) {
        Map<String, Double> filtered = new LinkedHashMap<>();
        for (String key : keys) {
            Double value = allPredictions.getOrDefault(key, 0.0);
            if (value > 0.1) { // Отсекаем шум
                filtered.put(key, Math.round(value * 1000.0) / 1000.0); // Округляем до 3 знаков
            }
        }
        return filtered;
    }

    /**
     * Получение книг пользователя через book-service
     */
    private List<BookDto> fetchUserBooks(UUID userId) {
        log.debug("Fetching books for user: {}", userId);

        try {
            List<BookDto> books = (List<BookDto>) this.bookInteractionClient
                    .sendRequest(HttpMethod.GET, "/" + userId)
                    .getBody();

            log.debug("Fetched {} books for user: {}",
                    books != null ? books.size() : 0, userId);

            return books != null ? books : Collections.emptyList();

        } catch (Exception e) {
            log.error("Failed to fetch books for user: {}", userId, e);
            throw new RuntimeException("Book service unavailable", e);
        }
    }

    /**
     * Фильтрация только новых книг (которых ещё нет в БД)
     */
    private List<BookDto> filterNewBooks(UUID userId, List<BookDto> userBooks) {
        // Получаем ID уже проанализированных книг
        Set<UUID> analyzedBookIds = this.bookAnalysisRepository.findAnalyzedBookIds();

        // Оставляем только те, которых нет в БД
        return userBooks.stream()
                .filter(book -> !analyzedBookIds.contains(book.id()))
                .collect(Collectors.toList());
    }

    /**
     * Получить все существующие анализы пользователя
     */
    private List<BookAnalysisResponse> getExistingAnalyses(UUID userId) {
        return bookAnalysisRepository.findByUserId(userId);
    }

    /**
     * Получить все анализы пользователя (включая только что созданные)
     */
    private List<BookAnalysisResponse> getAllAnalysesForUser(UUID userId) {
        return bookAnalysisRepository.findByUserId(userId);
    }

    /**
     * Fallback метод для Circuit Breaker
     */
    private List<BookAnalysisResponse> fallbackForBookService(
            UUID userId, Throwable throwable) {
        log.error("Fallback activated for user: {}. Reason: {}",
                userId, throwable.getMessage());

        // Возвращаем кэшированные данные, если есть
        List<BookAnalysisResponse> cached = bookAnalysisRepository
                .findByUserId(userId);

        if (!cached.isEmpty()) {
            log.info("Returning cached analysis for user: {}", userId);
            return cached;
        }

        // Если кэша нет - возвращаем пустой список
        log.warn("No cached data available for user: {}", userId);
        return Collections.emptyList();
    }
}
