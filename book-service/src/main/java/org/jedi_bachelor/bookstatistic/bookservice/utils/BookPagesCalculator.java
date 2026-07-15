package org.jedi_bachelor.bookstatistic.bookservice.utils;

import org.jedi_bachelor.bookstatistic.bookservice.dto.BookMetrics;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookPagesCalculator {
    private static final int CHARS_PER_PAGE_DEFAULT = 1700;

    private static final int WORDS_PER_PAGE_DEFAULT = 280;

    private static final double MEAN_PAGE_READING_TIME = 220.0;

    /**
     * Метод для подсчёта кол-ва страниц в книге
     *
     * @param content содержание файла (чистый текст)
     * @return метрики для файла
     */
    public BookMetrics calculateMetrics(UUID bookId, String content) {
        int totalChars = content.length();
        int totalWords = content.split("\\s+").length;

        int pagesByChars = (int) Math.ceil((double) totalChars / CHARS_PER_PAGE_DEFAULT);
        int pagesByWords = (int) Math.ceil((double) totalWords / WORDS_PER_PAGE_DEFAULT);

        int averagePages = (pagesByChars + pagesByWords) / 2;

        int readingTimeMinutes = (int) Math.ceil(totalWords / MEAN_PAGE_READING_TIME);

        return BookMetrics.builder()
                .pages(averagePages)
                .pagesByChars(pagesByChars)
                .pagesByWords(pagesByWords)
                .totalChars(totalChars)
                .totalWords(totalWords)
                .readingTimeMinutes(readingTimeMinutes)
                .build();
    }
}
