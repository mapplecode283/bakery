package com.mooshi.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestCorrelationFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders()
                .getFirst(CORRELATION_ID);
        final String cid = (correlationId != null) ? correlationId : UUID.randomUUID().toString();

        MDC.put("correlationId", cid);

        exchange.getResponse().getHeaders()
                .add(CORRELATION_ID, cid);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.header(CORRELATION_ID, cid))
                .build();

        return chain.filter(mutatedExchange)
                .doFinally(s -> MDC.remove("correlationId"));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
