package org.jedi_bachelor.bookstatistic.gateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                String traceId = this.tracer.currentSpan().context().traceId();

                log.info("Adding correlation-id into headers. trace id : {}", traceId);

                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = this.filterUtils.getCorrelationId(requestHeaders);

                log.info("Adding the correlation id to the outbound headers. {}", correlationId);

                this.filterUtils.setCorrelationId(exchange, correlationId);

                log.info("Completing outgoing request for {}.", exchange.getRequest().getURI());

                String language = this.filterUtils.getAcceptLanguage(requestHeaders);

                log.info("Adding the ACCEPT-LANGUAGE to the outbound headers. {}", correlationId);

                this.filterUtils.setAcceptLanguage(exchange, language);
            }));
        };
    }
}