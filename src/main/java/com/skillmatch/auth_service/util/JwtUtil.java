package com.skillmatch.auth_service.util;

import com.skillmatch.auth_service.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String secret;
    private Key signingKey;

    private static final long EXPIRATION_MILLIS = Duration.ofHours(1).toMillis();
    private static final long CLOCK_SKEW_SECONDS = 30;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    @PostConstruct
    public void initKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes for HS256");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // ---- Generate token with claims: uid, name, caps ----
    public String generateToken(User user) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("uid", user.getId())
                .claim("name", user.getFullName())
                .claim("caps", user.getCapabilities()) // serializes to ["LEARN","TEACH", ...]
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXPIRATION_MILLIS))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = parseAllClaims(token);
        return resolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
