package com.pubx.authservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiry}")
    private long accessTokenExpiryMs;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiryMs;

    // ─────────────────────────────────────────────
    //  Access Token — short-lived (15 min)
    //  Sent with every API request in header:
    //  Authorization: Bearer eyJhbGciOi...
    // ─────────────────────────────────────────────
    public String generateAccessToken(UUID userId, String email, String role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .claim("type", "ACCESS")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ─────────────────────────────────────────────
    //  Refresh Token — long-lived (7 days)
    //  Only used to get a new access token
    //  Also a JWT (so it carries userId + expiry)
    // ─────────────────────────────────────────────
    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiryMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ─────────────────────────────────────────────
    //  Validate — returns true if token is valid
    // ─────────────────────────────────────────────
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // Extract userId
    public UUID extractUserId(String token) {
        return UUID.fromString(extractAllClaims(token).getSubject());
    }

    // Extract email
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // Extract role
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Extract token type — "ACCESS" or "REFRESH"
    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    // Extract expiry
    public Date extractExpiry(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // Check if expired
    public boolean isTokenExpired(String token) {
        return extractExpiry(token).before(new Date());
    }

    // ─────── PRIVATE HELPERS ───────

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}