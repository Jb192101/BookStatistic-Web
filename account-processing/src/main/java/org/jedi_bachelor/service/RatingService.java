package org.jedi_bachelor.service;

import org.jedi_bachelor.math.MathDistribution;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.AccountBookSpeedReading;
import org.jedi_bachelor.model.entities.AchievementAccountRelation;
import org.jedi_bachelor.repository.AccountBookRelationShipRepository;
import org.jedi_bachelor.repository.AccountBookSpeedReadingRepository;
import org.jedi_bachelor.repository.AccountRepository;
import org.jedi_bachelor.repository.AchievementAccountRelationRepository;
import org.jedi_bachelor.service.interfaces.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService implements IRatingService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MathDistribution mathDistribution;
    @Autowired
    private AccountBookRelationShipRepository accountBookRelationShipRepository;
    @Autowired
    private AccountBookSpeedReadingRepository accountBookSpeedReadingRepository;
    @Autowired
    private AchievementAccountRelationRepository achievementAccountRelationRepository;

    /*
    * Метод установки нового рейтинга
    * @param id (Long) - ID пользователя, чей рейтинг надо поменят
     */
    @Override
    public void setNewRating(Long id) {
        Optional<Account> account = this.accountRepository.findById(id);

        if(account.isPresent()) {
            List<AccountBookSpeedReading> relationsSpeed = accountBookSpeedReadingRepository.findByAccountId(id);

            // Подсчёт новых страниц
            Integer newPages = 0;
            for (AccountBookSpeedReading r : relationsSpeed) {
                if (r.getDate().isAfter(LocalDateTime.now().minusWeeks(1)))
                    newPages += r.getSpeedReadingPages();
            }

            // Подсчёт новых достижений
            Integer newAchievements = 0;
            List<AchievementAccountRelation> rel = achievementAccountRelationRepository.findByAccountId(id);
            for (AchievementAccountRelation r : rel) {
                if (r.getDateOfGetting().isAfter(LocalDateTime.now().minusWeeks(1)))
                    newAchievements++;
            }

            account.get().setRating(recalculateRating(account.get().getRating(), newPages, newAchievements));
            accountRepository.save(account.get());
        }
    }

    /*
    * Метод перевычисления рейтинга
    * @param oldValueOfDistribuion (Integer) - старое значение рейтинга
    * @param newPages (Integer) - количество новых страниц за неделю
    * @param newAchievements (Integer) - количество новых достижений
    * @return значение нового рейтинга
     */
    @Override
    public Integer recalculateRating(Integer oldValueOfDistribution, Integer newPages, Integer newAchievements) {
        Float x = mathDistribution.x(newAchievements, newPages, oldValueOfDistribution);
        oldValueOfDistribution += mathDistribution.newRating(x);

        return mathDistribution.newRating(oldValueOfDistribution);
    }
}
