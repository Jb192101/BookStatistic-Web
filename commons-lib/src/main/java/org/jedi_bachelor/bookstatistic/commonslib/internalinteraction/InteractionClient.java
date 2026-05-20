package org.jedi_bachelor.bookstatistic.commonslib.internalinteraction;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Класс для клиентов внутреннего взаимодействия (отправка REST-запросов
 * на выполнение каких-либо действий)
 */
public class InteractionClient {
    private final RestClient restClient;

    /**
     * Базовый конструктор класса клиента взаимодействия
     *
     * @param baseUrl базовый URL службы, с которой происходит взаимодействие
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
    @Retry(name = "commonslib-retry")
    public ResponseEntity<?> sendRequest(HttpMethod httpMethod, String url) {
       return this.restClient.method(httpMethod)
                    .uri(url)
                    .retrieve()
                    .toEntity(ResponseEntity.class);
    }

    /**
     * Метод отправки запроса в микросервис (с телом)
     *
     * @param httpMethod метод HTTP (POST/GET/DELETE/PUT/PATCH)
     * @param url url
     * @param body тело запроса
     * @return тело ответа
     */
    @Retry(name = "commonslib-retry")
    public ResponseEntity<?> sendRequest(HttpMethod httpMethod, String url, Object body) {
        return this.restClient.method(httpMethod)
                .uri(url)
                .body(body)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }
}
