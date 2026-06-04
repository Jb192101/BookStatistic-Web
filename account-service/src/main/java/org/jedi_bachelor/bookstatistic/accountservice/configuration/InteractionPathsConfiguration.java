package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class InteractionPathsConfiguration {
    @Value("${external-urls.analyze.base-url}")
    private String analyzeBasePath;

    @Value("${external-urls.notification.base-url}")
    private String notificationBasePath;

    private AnalyzePaths analyzePaths;

    private NotificationPaths notificationPaths;

    @Getter
    public static class AnalyzePaths {
        @Value("${external-urls.analyze.delete-user-data}")
        private String deleteUserDataPath;
    }

    @Getter
    public static class NotificationPaths {
        @Value("${external-urls.notification.post-notification-settings}")
        private String notificationSettingsPostPath;
    }
}
