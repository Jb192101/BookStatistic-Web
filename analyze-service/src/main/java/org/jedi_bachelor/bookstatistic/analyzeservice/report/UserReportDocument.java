package org.jedi_bachelor.bookstatistic.analyzeservice.report;

/**
 * Класс, агрегирующий в себе всю необходимую для анализа пользователя
 *
 * Информация агрегируется при запросе в analyze-service на анализ пользователя
 * Собирается информация из account-service и book-service
 */

import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Document(collection = "user_reports")
@Data
@RequiredArgsConstructor
public class UserReportDocument {
    @Id
    private UUID userId;

    private String username;

    private LocalDate birthday;

    private String residenceRegion;

    private List<BookStats> bookStatsList;

    private List<UserResponseStats> userResponseStatsList;

    private Map<String, Double> genrePreferences;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Data
    public static class BookStats {
        private UUID bookId;
        private String bookName;
        private Integer readedPercent;
    }

    @Data
    public static class UserResponseStats {
        private UUID bookId;
        private String responseText;
        private Integer starsCount;
    }
}
