package org.jedi_bachelor.bookstatistic.analyzeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "book_analyze_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAnalyzeResult {
    @EmbeddedId
    private BookAnalyzeResultId id;

    @Column(name = "analyze_date")
    private LocalDateTime analyzeDate;

    @Column(name = "book_analyze_data", columnDefinition = "jsonb")
    private String bookAnalyzeData;

    @Transient
    public Long getTextVersionNumber() {
        return this.id.getTextVersionNumber();
    }

    @Transient
    public UUID getBookId() {
        return this.id.getBookId();
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookAnalyzeResultId implements Serializable {
        @Column(name = "book_id")
        private UUID bookId;

        @Column(name = "text_version_number")
        private Long textVersionNumber;
    }
}
