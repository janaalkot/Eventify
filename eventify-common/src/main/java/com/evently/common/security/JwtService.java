package com.evently.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(AuthenticatedUser authenticatedUser) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(jwtProperties.getExpirationMs());

        return Jwts.builder()
                .setSubject(authenticatedUser.email())
                .addClaims(Map.of(
                        "userId", authenticatedUser.id(),
                        "role", authenticatedUser.role(),
                        "fullName", authenticatedUser.fullName()
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public AuthenticatedUser extractUser(String token) {
        Claims claims = parseClaims(token);
        Long userId = claims.get("userId", Number.class).longValue();
        String role = claims.get("role", String.class);
        String fullName = claims.get("fullName", String.class);
        return new AuthenticatedUser(userId, claims.getSubject(), role, fullName);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }
}
