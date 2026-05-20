package org.jedi_bachelor.bookstatistic.analyzeservice.jsonmap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreferencesEntity {
    private List<UserPreferences> userPreferencesList;

    public static class UserPreferences {
        @JsonProperty(required = true)
        private UUID userId;

        @JsonProperty(required = true)
        private Map<String, Integer> literaturePreferences;
    }
}
