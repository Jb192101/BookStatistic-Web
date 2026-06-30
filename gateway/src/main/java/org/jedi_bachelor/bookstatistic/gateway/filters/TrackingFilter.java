package org.jedi_bachelor.bookstatistic.gateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.gateway.utils.FilterUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Order(1)
@Component
@Slf4j
@RequiredArgsConstructor
public class TrackingFilter implements GlobalFilter {
    private final FilterUtils filterUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        // Correlation ID
        if (this.isCorrelationIdPresent(requestHeaders)) {
            log.info("tmx-correlation-id found in tracking filter: {}. ", this.filterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationId = this.generateCorrelationId();
            exchange = this.filterUtils.setCorrelationId(exchange, correlationId);

            log.info("tmx-correlation-id generated in tracking filter: {}.", correlationId);
        }

        // Accept Language
        if(this.isAcceptLanguagePresent(requestHeaders)) {
            log.info("Accept-Language found in tracking filter: {}. ", this.filterUtils.getAcceptLanguage(requestHeaders));
        } else {
            String defaultLanguage = "EN";
            exchange = this.filterUtils.setAcceptLanguage(exchange, defaultLanguage);

            log.info("Accept-Language set to default: {}.", defaultLanguage);
        }

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        return this.filterUtils.getCorrelationId(requestHeaders) != null;
    }

    private boolean isAcceptLanguagePresent(HttpHeaders headers) {
        return this.filterUtils.getAcceptLanguage(headers) != null;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
