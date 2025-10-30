package org.jedi_bachelor.config;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookConfig {
    @Bean
    public LevenshteinDistance levenshtein() {
        return new LevenshteinDistance();
    }
}
