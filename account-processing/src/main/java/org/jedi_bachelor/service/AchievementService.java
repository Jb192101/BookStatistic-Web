package org.jedi_bachelor.service;

/*
Сервис для работы с достижениями
 */

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.Achievement;
import org.jedi_bachelor.model.entities.AchievementAccountRelation;
import org.jedi_bachelor.repository.AccountBookSpeedReadingRepository;
import org.jedi_bachelor.repository.AchievementAccountRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {
    @Autowired
    private AchievementAccountRelationRepository achievementAccountRelationRepository;
    @Autowired
    private AccountBookSpeedReadingRepository accountBookSpeedReadingRepository;

    private final List<Achievement> achievements;

    /*
    Метод для выдачи достижений пользователя
     */
    public List<Achievement> getAchievementsOfUser(Long id) {
        List<Achievement> achieved = getAchievedAchievementsOfUser(id);
        List<Achievement> notAchieved = getNotAchievedAchievementsOfUser(id);

        List<Achievement> all = new ArrayList<>();

        all.addAll(achieved);
        all.addAll(notAchieved);

        return all;
    }

    /*
    Метод для выдачи полученных пользователем достижений
     */
    private List<Achievement> getAchievedAchievementsOfUser(Long id) {
        List<Achievement> result = new ArrayList<>();

        for(Achievement a : achievements) {
            if(a.isAchieved(id)) {
                result.add(a);
            }
        }

        return result;
    }

    /*
    Метод для выдачи ещё не достигнутых пользователем достижений
     */
    private List<Achievement> getNotAchievedAchievementsOfUser(Long id) {
        List<Achievement> result = new ArrayList<>();

        for(Achievement a : achievements) {
            if(!a.isAchieved(id)) {
                result.add(a);
            }
        }

        return result;
    }

    /*
    Метод для проверки новых достижений у пользователя
     */
    public void checkNewAchievements(Account account) {
        List<AchievementAccountRelation> relations = achievementAccountRelationRepository.findByAccountId(account.getId());

        for(Achievement a : achievements) {
            if(a.isAchieved(account.getId()) &&
            !achievementAccountRelationRepository.existsByAccountIdAndAchievementId(account.getId(), a.getId())) {

                AchievementAccountRelation achievementAccountRelation = new AchievementAccountRelation();
                achievementAccountRelation.setAchievementId(a.getId());
                achievementAccountRelation.setAccount(account);
                achievementAccountRelation.setDateOfGetting(LocalDateTime.now());

                achievementAccountRelationRepository.save(achievementAccountRelation);
            }
        }
    }
}
