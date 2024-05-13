package com.renzo.filter.global.post;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PostGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("Executing Post Filter Logic");
            exchange.getResponse().getHeaders().add("X-Post-Header", "PostFilter");
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Post Filter 순서 설정 (Pre Filter보다 나중에 실행)
    }
}
