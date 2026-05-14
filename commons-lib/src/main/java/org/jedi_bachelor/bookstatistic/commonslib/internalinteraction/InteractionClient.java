package org.jedi_bachelor.bookstatistic.commonslib.internalinteraction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Класс для клиентов внутреннего взаимодействия (отправка REST-запросов
 * на выполнение каких-либо действий)
 */
public class InteractionClient {
    protected final RestClient restClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    private String serviceName;

    /**
     * Базовый конструктор класса клиента взаимодействия
     *
     * @param baseUrl базовый URL службы, с которой происходит взаимодействие
     * @param headers заголовки запросов
     * @param serviceName имя сервиса, с которым должна настроиться связь (для Spring Eureka)
     */
    public InteractionClient(String baseUrl, HttpHeaders headers, String serviceName) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(String.valueOf(headers))
                .build();

        this.serviceName = serviceName;
    }

    /**
     * Метод отправки запроса в микросервис
     *
     * @param httpMethod метод HTTP (POST/GET/DELETE/PUT/PATCH)
     * @param url url
     * @return тело ответа
     */
    public ResponseEntity<?> sendRequest(HttpMethod httpMethod, String url) {
        if(this.hasAtLeastOneInstance()) {
            return this.restClient.method(httpMethod)
                    .uri(url)
                    .retrieve()
                    .toEntity(ResponseEntity.class);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Метод для проверки наличия хотя бы одного экземпляра нужного сервиса
     *
     * @return true, если есть
     */
    private boolean hasAtLeastOneInstance() {
        List<ServiceInstance> instances =
                this.discoveryClient.getInstances(this.serviceName);

        return !instances.isEmpty();
    }
}
