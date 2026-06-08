package com.integracion.camel_spotify.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

@Component
class JwtUtil {

    @Value('${jwt.secret}')
    String secret

    @Value('${jwt.expiration-ms:3600000}')
    long expirationMs

    private getSigningKey() {
        Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))
    }

    String generateToken(String clientId) {
        Jwts.builder()
            .subject(clientId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSigningKey())
            .compact()
    }

    String extractClientId(String token) {
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
            return true
        } catch (Exception e) {
            return false
        }
    }
}
