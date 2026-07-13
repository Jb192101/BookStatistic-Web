package org.jedi_bachelor.bookstatistic.analyzeservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class InteractionPathsConfiguration {
    @Value("${external-urls.account.base-url}")
    private String accountBaseUrl;

    @Value("${external-urls.account.get-account-uri}")
    private String accountGetUri;

    @Value("${external-urls.book.book-base-url}")
    private String bookBaseUrl;

    @Value("${external-urls.book.get-users-book-uri}")
    private String bookGetUsersBooksUri;

    @Value("${external-urls.book.response-base-url}")
    private String responseBaseUrl;

    @Value("${external-urls.book.get-users-responses-uri}")
    private String responseGetUsersResponsesUri;
}
