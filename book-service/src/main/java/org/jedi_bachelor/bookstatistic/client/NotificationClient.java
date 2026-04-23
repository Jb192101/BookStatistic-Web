package org.jedi_bachelor.bookstatistic.client;

import jakarta.annotation.PostConstruct;
import org.jedi_bachelor.bookstatistic.dto.request.notification.NotificationCreationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationClient {
    private RestClient restClient;

    @Value("${external-url.notification.base-url}")
    private String baseUrl;

    @Value("${external-url.notification.add-new-notification}")
    private String addNewNotificationAddress;

    @PostConstruct
    public void postConstruct() {
        this.restClient = RestClient.builder()
                .baseUrl(this.baseUrl)
                .build();
    }

    /**
     * Метод отправки REST-запроса на создание нового уведомления
     *
     * @param dto DTO создания уведомления
     */
    public void addNewNotification(NotificationCreationDto dto) {
        this.restClient.method(HttpMethod.POST)
                .uri(this.addNewNotificationAddress)
                .body(dto);
    }
}
