package com.evently.api_gateway.security;

import com.evently.common.security.AuthenticatedUser;
import com.evently.common.security.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtGatewayFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (isPublicRoute(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        AuthenticatedUser authenticatedUser = jwtService.extractUser(token);
        if (requiresAdmin(path, method) && !authenticatedUser.isAdmin()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-Auth-User-Id", authenticatedUser.id().toString())
                .header("X-Auth-Email", authenticatedUser.email())
                .header("X-Auth-Role", authenticatedUser.role())
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicRoute(String path, HttpMethod method) {
        if (path.equals("/auth/register") || path.equals("/auth/login")) {
            return true;
        }

        return method == HttpMethod.GET && (path.equals("/events") || path.startsWith("/events/"));
    }

    private boolean requiresAdmin(String path, HttpMethod method) {
        return path.startsWith("/events") && method != HttpMethod.GET;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
