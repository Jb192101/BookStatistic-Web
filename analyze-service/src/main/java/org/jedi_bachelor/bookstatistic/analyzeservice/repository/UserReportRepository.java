package org.jedi_bachelor.bookstatistic.analyzeservice.repository;

import org.jedi_bachelor.bookstatistic.analyzeservice.report.UserReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserReportRepository extends MongoRepository<UserReportDocument, UUID> {
    List<UserReportDocument> findByUserId(UUID userId);
}
