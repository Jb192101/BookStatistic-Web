package org.jedi_bachelor.bookstatistic.analyzeservice.repository;

import org.jedi_bachelor.bookstatistic.analyzeservice.entity.TrainingData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrainingDataRepository extends JpaRepository<TrainingData, UUID> {
    List<TrainingData> findByUsedForTrainingFalse();
}
