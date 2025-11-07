package org.jedi_bachelor.config;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jedi_bachelor.math.MathDistribution;
import org.jedi_bachelor.math.MathDistributionImpl;
import org.jedi_bachelor.model.entities.Achievement;
import org.jedi_bachelor.service.AchievementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BookConfig {
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
        achievementList.add(new Achievement("1000 страниц!", "Прочитай за неделю 1000 страниц"));

        return new AchievementService(achievementList);
    }
}
