package org.jedi_bachelor.bookstatistic.notificationservice.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class InteractionPathsConfiguration {
    @Value("${external-urls.account.base-url}")
    private String accountBasePath;

    private AccountPaths accountPaths;

    @Getter
    public static class AccountPaths {

    }
}
