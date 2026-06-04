package org.jedi_bachelor.bookstatistic.bookservice.configuration;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class InteractionPathsConfiguration {
    private String analyzeBasePath;

    private String notificationBasePath;

    private AnalyzePaths analyzePaths;

    private NotificationPaths notificationPaths;

    @Getter
    public static class AnalyzePaths {

    }

    @Getter
    public static class NotificationPaths {

    }
}
