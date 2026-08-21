package com.auto.tutorial.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtService {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Valide un token JWT
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("Token invalide: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si le token est expiré
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification d'expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Extrait l'ID utilisateur du token (stocké dans le subject "sub")
     */
    public String getUserId(String token) {
        Claims claims = getAllClaims(token);
        return claims.getSubject();
    }

    /**
     * Extrait l'email du token (stocké dans le claim "email")
     */
    public String getEmail(String token) {
        Claims claims = getAllClaims(token);
        return claims.get("email", String.class);
    }

    /**
     * Extrait le rôle du token (stocké dans le claim "role")
     */
    public String getRole(String token) {
        Claims claims = getAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extrait tous les claims du token
     */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}