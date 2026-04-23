package org.jedi_bachelor.bookstatistic.internalinteraction;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Класс для клиентов внутреннего взаимодействия (отправка REST-запросов
 * на выполнение каких-либо действий)
 */
public class InteractionClient {
    protected final RestClient restClient;

    /**
     * Базовый конструктор класса клиенат взаимодействия
     *
     * @param baseUrl базовый URL
     * @param headers заголовки запросов
     */
    public InteractionClient(String baseUrl, HttpHeaders headers) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(String.valueOf(headers))
                .build();
    }

    /**
     * Метод отправки запроса в микросервис
     *
     * @param httpMethod метод HTTP (POST/GET/DELETE/PUT/PATCH)
     * @param url url
     * @return тело ответа
     */
    public ResponseEntity<?> sendRequest(HttpMethod httpMethod, String url) {
        return this.restClient.method(httpMethod)
                .uri(url)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }
}
