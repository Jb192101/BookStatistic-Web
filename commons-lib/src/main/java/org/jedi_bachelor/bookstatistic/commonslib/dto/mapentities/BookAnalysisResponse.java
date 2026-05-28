package org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class BookAnalysisResponse {
    private UUID analysisId;
    private UUID bookId;
    private String bookTitle;
    private LocalDateTime analyzedAt;

    // Топ-3 основных жанра
    private List<GenreScore> topGenres;

    // Значимые поджанры (с порогом > 0.5)
    private List<GenreScore> significantSubgenres;

    // Характеристики для UI
    private AtmosphereSummary atmosphere;
    private PacingSummary pacing;

    // Для отладки и визуализации
    private Double confidenceScore;

    @Data
    @Builder
    public static class GenreScore {
        private String genre;
        private Double score;
        private String displayName;

        // Пример: "detective" -> "Детектив (0.91)"
        public String getFormatted() {
            return String.format("%s (%.2f)", displayName, score);
        }
    }

    @Data
    @Builder
    public static class AtmosphereSummary {
        private String primary;     // "Мрачная и напряженная"
        private String secondary;   // "с элементами загадочности"
        private Map<String, Double> all; // Все значения для графиков
    }

    @Data
    @Builder
    public static class PacingSummary {
        private String description; // "Быстрый темп с частыми экшн-сценами"
        private Double overallScore;
    }
}
