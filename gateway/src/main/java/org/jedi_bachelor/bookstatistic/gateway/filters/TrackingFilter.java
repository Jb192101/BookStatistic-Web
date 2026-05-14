package org.jedi_bachelor.bookstatistic.gateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        if (this.isCorrelationIdPresent(requestHeaders)) {
            log.info("tmx-correlation-id found in tracking filter: {}. ", filterUtils.getCorrelationId(requestHeaders));
        } else {
            String correlationID = this.generateCorrelationId();
            exchange = filterUtils.setCorrelationId(exchange, correlationID);

            log.info("tmx-correlation-id generated in tracking filter: {}.", correlationID);
        }

        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        return filterUtils.getCorrelationId(requestHeaders) != null;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
