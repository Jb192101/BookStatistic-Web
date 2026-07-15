package org.jedi_bachelor.bookstatistic.accountservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class InteractionPathsConfiguration {
    @Value("${external-urls.analyze.base-url}")
    private String analyzeBasePath;

    @Value("${external-urls.notification.base-url}")
    private String notificationBasePath;

    @Value("${external-urls.analyze.delete-user-data}")
    private String deleteUserDataPath;

    @Value("${external-urls.notification.post-notification-settings}")
    private String notificationSettingsPostPath;
}
