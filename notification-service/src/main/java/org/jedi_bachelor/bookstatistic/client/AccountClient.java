package org.jedi_bachelor.bookstatistic.client;

import jakarta.annotation.PostConstruct;
import org.jedi_bachelor.bookstatistic.dto.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AccountClient {
    private RestClient restClient;

    @Value("${external-url.account.base-url}")
    private String baseUrl;

    @Value("${external-url.account.emails}")
    private String emailsRequestAddress;

    @PostConstruct
    public void postConstruct() {
        this.restClient = RestClient.builder()
                .baseUrl(this.baseUrl)
                .build();
    }

    public List<String> getEmailAddresses() {
        ResponseEntity<SuccessResponse> responseEntity = this.restClient.method(HttpMethod.GET)
                .uri(this.emailsRequestAddress)
                .retrieve()
                .toEntity(SuccessResponse.class);

        return (List<String>) responseEntity.getBody().content();
    }
}
