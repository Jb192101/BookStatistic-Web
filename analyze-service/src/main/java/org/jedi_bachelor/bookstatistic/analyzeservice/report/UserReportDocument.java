package org.jedi_bachelor.bookstatistic.analyzeservice.report;

/**
 * Класс, агрегирующий в себе всю необходимую для анализа пользователя
 *
 * Информация агрегируется при запросе в analyze-service на анализ пользователя
 * Собирается информация из account-service и book-service
 */

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class UserReportDocument {
    private UUID userId;

    private String username;

    private LocalDate birthday;

    private String residenceRegion; // Страна проживания

    private List<BookStats> bookStatsList;

    private List<UserResponseStats> userResponseStatsList;

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
