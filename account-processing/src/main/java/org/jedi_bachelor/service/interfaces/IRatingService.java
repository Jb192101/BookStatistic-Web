package org.jedi_bachelor.service.interfaces;

public interface IRatingService {
    void setNewRating(Long id);
    Integer recalculateRating(Integer oldValueOfDistribution, Integer newPages, Integer newAchievements);
}
