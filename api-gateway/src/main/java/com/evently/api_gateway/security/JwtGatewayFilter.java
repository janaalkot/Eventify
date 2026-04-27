package com.evently.api_gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;

@Component
@Order(-1)
public class JwtGatewayFilter implements GlobalFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        System.out.println("✅ JWT GATEWAY FILTER LOADED");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        System.out.println("\n🔐 Incoming Request: " + method + " " + path);

        // =========================
        // ✅ PUBLIC ENDPOINTS
        // =========================
        if (path.contains("/users/login") || path.contains("/users/register")) {
            System.out.println("✅ Public endpoint - skipping authentication");
            return chain.filter(exchange);
        }

        // =========================
        // 🔑 GET TOKEN
        // =========================
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // =========================
        // ❌ VALIDATE TOKEN
        // =========================
        if (!jwtUtil.isTokenValid(token)) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        // =========================
        // ✅ EXTRACT USER INFO
        // =========================
        final String email = jwtUtil.extractAllClaims(token).getSubject();
        String extractedRole = jwtUtil.extractRole(token);
        final String role = (extractedRole != null) ? extractedRole.toUpperCase().trim() : "USER";

        System.out.println("👤 Authenticated User: " + email);
        System.out.println("👤 Role: " + role);

        // =========================
        // 🔒 ROLE-BASED AUTHORIZATION
        // =========================

        // ADMIN ONLY → /users/admin/**
        if (path.contains("/users/admin") && !role.equals("ADMIN")) {
            return forbidden(exchange, "Admin access required");
        }

        // DELETE EVENTS → ADMIN ONLY
        if (method.equals("DELETE") && path.contains("/api/events") && !role.equals("ADMIN")) {
            return forbidden(exchange, "Only ADMIN can delete events");
        }

        // CREATE & UPDATE EVENTS → ADMIN or EMPLOYEE
        if ((method.equals("POST") || method.equals("PUT")) &&
                path.contains("/api/events") &&
                !(role.equals("ADMIN") || role.equals("EMPLOYEE"))) {

            return forbidden(exchange, "Only ADMIN or EMPLOYEE can modify events");
        }

        // =========================
        // ✅ ADD USER INFO TO HEADERS
        // =========================
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                )
                .build();

        System.out.println("✅ Request authorized and forwarded");

        return chain.filter(modifiedExchange);
    }

    // =========================
    // ❌ 401 UNAUTHORIZED
    // =========================
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        System.out.println("❌ 401 Unauthorized: " + message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    // =========================
    // ❌ 403 FORBIDDEN
    // =========================
    private Mono<Void> forbidden(ServerWebExchange exchange, String message) {
        System.out.println("⛔ 403 Forbidden: " + message);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}