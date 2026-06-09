package org.jedi_bachelor.bookstatistic.gateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import io.micrometer.tracing.Tracer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ResponseFilter {
    private final FilterUtils filterUtils;

    private final Tracer tracer;

    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) ->
                chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

                    String correlationId = filterUtils.getCorrelationId(requestHeaders);
                    if (correlationId != null) {
                        log.debug("Correlation id: {}", correlationId);
                        ServerWebExchange mutatedExchange = filterUtils.setCorrelationId(exchange, correlationId);
                    }

                    String language = filterUtils.getAcceptLanguage(requestHeaders);
                    log.debug("Accept-Language: {}", language);

                    String userId = this.filterUtils.getUserId(requestHeaders);
                    if(userId != null) {
                        log.debug("User ID: {}", userId);
                        ServerWebExchange mutatedExchange = filterUtils.setCorrelationId(exchange, userId);
                    }

                    log.debug("Completing outgoing request for: {}", exchange.getRequest().getURI());
                }));
    }
}