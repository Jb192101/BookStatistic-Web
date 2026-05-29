package org.jedi_bachelor.bookstatistic.analyzeservice.repository;

import org.jedi_bachelor.bookstatistic.analyzeservice.entity.BookAnalysis;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAnalysisResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BookAnalysisRepository extends JpaRepository<BookAnalysis, UUID> {
    List<BookAnalysisResponse> findByUserId(UUID userId);

    Set<UUID> findAnalyzedBookIds();
}
