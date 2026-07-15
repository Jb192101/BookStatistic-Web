package org.jedi_bachelor.bookstatistic.analyzeservice.repository;

import org.jedi_bachelor.bookstatistic.analyzeservice.entity.BookAnalyzeResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookAnalyzeResultRepository
        extends JpaRepository<BookAnalyzeResult, BookAnalyzeResult.BookAnalyzeResultId> {
}
