package com.evently.user_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkeymysecretkeymysecretkeymysecretkey";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // CREATE TOKEN
    public String generateToken(String email, String role) {
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        System.out.println("Generated token for: " + email + " with role: " + role);
        return token;
    }

    // EXTRACT EMAIL
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // EXTRACT ROLE
    public String extractRole(String token) {
        String role = parseClaims(token).get("role", String.class);
        System.out.println("Extracted role: " + role);
        return role;
    }

    // VALIDATE TOKEN
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            System.out.println("Token is valid");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Token invalid: " + e.getMessage());
        }
        return false;
    }

    // COMMON PARSER
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}