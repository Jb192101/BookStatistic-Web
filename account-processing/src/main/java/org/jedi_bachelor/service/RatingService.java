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

    /*
    public void calculateNewRating(BookRatingDto bookDto) {
        Account account = accountRepository.getAccountById(bookDto.getUserId());

        Integer addedValueRating = bookDto.getNewReadedPages() * 2;

        account.setRating(account.getRating() + addedValueRating);

        accountRepository.save(account);
    }
     */

    public void setNewRating(Long id) {
        Optional<Account> account = this.accountRepository.getAccountById(id);

        List<AccountBookRelationShip> relations = accountBookRelationShipRepository.findByAccountId(id);
        List<AccountBookSpeedReading> relationsSpeed = accountBookSpeedReadingRepository.findByAccountId(id);

        // Подсчёт всего прочитанных страниц
        Integer allReadedPages = 0;
        for(AccountBookRelationShip relation : relations) {
            allReadedPages += relation.getReadedPages();
        }

        // Подсчёт новых страниц
        Integer newPages = 0;
        for(AccountBookSpeedReading r : relationsSpeed) {
            if(r.getDate().isAfter(LocalDateTime.now().minusWeeks(1)))
                newPages += r.getSpeedReadingPages();
        }

        // Подсчёт новых достижений
        Integer newAchievements = 0;
        List<AchievementAccountRelation> rel = achievementAccountRelationRepository.findByAccountId(id);
        for(AchievementAccountRelation r : rel) {
            if(r.getDateOfGetting().isAfter(LocalDateTime.now().minusWeeks(1)))
                newAchievements++;
        }

        account.get().setRating(recalculateRating(account.get().getDistribution(), newPages, allReadedPages, newAchievements));
        accountRepository.save(account.get());
    }

    private Float recalculateRating(Float oldValueOfDistribution, Integer newPages, Integer allReadedPages, Integer newAchievements) {
        Float x = mathDistribution.x(newPages, allReadedPages, newAchievements);
        oldValueOfDistribution += x;

        return mathDistribution.rating(oldValueOfDistribution);
    }
}
