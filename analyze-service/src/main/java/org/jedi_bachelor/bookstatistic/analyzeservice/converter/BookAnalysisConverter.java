package org.jedi_bachelor.bookstatistic.analyzeservice.converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookAnalysisConverter {
    public Map<String, Double> getGenresAsMap(String genres) {
        return this.parseJsonToMap(genres);
    }

    public List<String> getTopGenres(String genres, int limit) {
        return this.getGenresAsMap(genres).entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<String> getSignificantGenres(String genres, double threshold) {
        return this.getGenresAsMap(genres).entrySet().stream()
                .filter(e -> e.getValue() >= threshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Map<String, Double> parseJsonToMap(String json) {
        if (json == null) return Map.of();

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, Double>>() {});
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
