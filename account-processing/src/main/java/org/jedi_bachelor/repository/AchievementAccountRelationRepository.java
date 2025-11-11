package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.AchievementAccountRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementAccountRelationRepository extends JpaRepository<AchievementAccountRelation, Long> {
    List<AchievementAccountRelation> findByAccountId(Long id);
    boolean existsByAccountIdAndAchievementId(Long accountId, Long achievementId);
}
