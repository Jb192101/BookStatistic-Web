package org.jedi_bachelor.bookstatistic.commonslib.internalinteraction;

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

    private final String baseUrl;

    /**
     * Базовый конструктор класса клиента взаимодействия
     *
     * @param baseUrl базовый URL
     * @param headers заголовки запросов
     */
    public InteractionClient(String baseUrl, HttpHeaders headers) {
        RestClient.Builder builder = RestClient.builder();

        if (headers != null && !headers.isEmpty()) {
            headers.forEach((name, values) -> {
                for (String value : values) {
                    builder.defaultHeader(name, value);
                }
            });
        }

        this.restClient = builder.build();

        this.baseUrl = baseUrl;
    }

    /**
     * Метод отправки запроса в микросервис
     *
     * @param httpMethod метод HTTP (POST/GET/DELETE/PUT/PATCH)
     * @param url url
     * @return тело ответа
     */
    @Retry(name = "commonslib-retry")
    public Object sendRequest(HttpMethod httpMethod, String url) {
       return this.restClient.method(httpMethod)
                    .uri(this.baseUrl + url)
                    .retrieve()
                    .toEntity(Object.class);
    }

    /**
     * Метод отправки запроса в микросервис (с телом)
     *
     * @param httpMethod метод HTTP (POST/GET/DELETE/PUT/PATCH)
     * @param url url
     * @param body тело запроса
     * @return тело ответа
     */
    //@Retry(name = "commonslib-retry")
    public ResponseEntity<?> sendRequest(HttpMethod httpMethod, String url, Object body) {
        return this.restClient.method(httpMethod)
                .uri(this.baseUrl + url)
                .body(body)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }

    /**
     * Метод отправки запроса в микросервис (с телом и с токеном)
     *
     * @param httpMethod метод HTTP (POST/GET/DELETE/PUT/PATCH)
     * @param url url
     * @param body тело запроса
     * @return тело ответа
     */
    @Retry(name = "commonslib-retry")
    public ResponseEntity<?> sendRequest(HttpMethod httpMethod, String url, Object body, String jwtToken) {
        return this.restClient.method(httpMethod)
                .uri(this.baseUrl + url)
                .body(body)
                .header("Authorization", "Bearer " + jwtToken)
                .retrieve()
                .toEntity(ResponseEntity.class);
    }
}
