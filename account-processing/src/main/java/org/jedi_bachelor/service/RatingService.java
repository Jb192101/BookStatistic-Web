package org.jedi_bachelor.service;

import org.jedi_bachelor.math.MathDistribution;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.jedi_bachelor.model.entities.AccountBookSpeedReading;
import org.jedi_bachelor.model.entities.AchievementAccountRelation;
import org.jedi_bachelor.repository.AccountBookRelationShipRepository;
import org.jedi_bachelor.repository.AccountBookSpeedReadingRepository;
import org.jedi_bachelor.repository.AccountRepository;
import org.jedi_bachelor.repository.AchievementAccountRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
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

    public void setNewRating(Long id) {
        Optional<Account> account = this.accountRepository.getAccountById(id);

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

    private Integer recalculateRating(Integer oldValueOfDistribution, Integer newPages, Integer newAchievements) {
        Float x = mathDistribution.x(newAchievements, newPages, oldValueOfDistribution);
        oldValueOfDistribution += mathDistribution.newRating(x);

        return mathDistribution.newRating(oldValueOfDistribution);
    }
}
