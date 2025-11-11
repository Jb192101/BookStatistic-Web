package org.jedi_bachelor.config;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jedi_bachelor.math.MathDistribution;
import org.jedi_bachelor.math.MathDistributionImpl;
import org.jedi_bachelor.model.entities.AccountBookRelationShip;
import org.jedi_bachelor.model.entities.AccountBookSpeedReading;
import org.jedi_bachelor.model.entities.Achievement;
import org.jedi_bachelor.repository.AccountBookRelationShipRepository;
import org.jedi_bachelor.repository.AccountBookSpeedReadingRepository;
import org.jedi_bachelor.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class BookConfig {
    @Autowired
    private AccountBookSpeedReadingRepository accountBookSpeedReadingRepository;
    @Autowired
    private AccountBookRelationShipRepository accountBookRelationShipRepository;

    @Bean
    public LevenshteinDistance levenshtein() {
        return new LevenshteinDistance();
    }

    @Bean
    public MathDistribution mathDistribution() {
        return new MathDistributionImpl();
    }

    // Достижения
    @Bean
    public AchievementService achievementService() {
        List<Achievement> achievementList = new ArrayList<>();

        // Список достижений
        // Достижение "1000 страниц!"
        achievementList.add(new Achievement(1L, "1000 страниц!", "Прочитай за неделю 1000 страниц") {
            @Override
            public Boolean isAchieved(Long userId) {
                List<AccountBookSpeedReading> speeds = accountBookSpeedReadingRepository.findByAccountId(userId);

                Integer pages = 0;
                for(AccountBookSpeedReading s : speeds) {
                    if(s.getDate().isAfter(LocalDateTime.now().minusWeeks(1)))
                        pages += s.getSpeedReadingPages();
                }

                if(pages >= 1000) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Достижение "С первой книгой"
        achievementList.add(new Achievement(1L, "С днём первой книги", "Полностью прочитать первую книгу") {
            @Override
            public Boolean isAchieved(Long userId) {
                List<AccountBookRelationShip> relations = accountBookRelationShipRepository.findByAccountId(userId);
                for(AccountBookRelationShip relation : relations) {
                    if(relation.getIsFinished()) {
                        return true;
                    }
                }

                return false;
            }
        });

        return new AchievementService(achievementList);
    }
}
