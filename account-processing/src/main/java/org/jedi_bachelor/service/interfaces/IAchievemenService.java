package org.jedi_bachelor.service.interfaces;

import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.Achievement;

import java.util.List;

public interface IAchievemenService {
    List<Achievement> getAchievementsOfUser(Long id);
    List<Achievement> getAchievedAchievementsOfUser(Long id);
    List<Achievement> getNotAchievedAchievementsOfUser(Long id);
    void checkNewAchievements(Account account);
}
