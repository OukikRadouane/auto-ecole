package com.auto.tutorial.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class SecurityExceptionHandler {

    /**
     * Gestion des accès refusés (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn(" Accès refusé : {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "Vous n'avez pas les droits nécessaires pour accéder à cette ressource");
        body.put("path", "tutorial-service");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Gestion des identifiants invalides (401)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn(" Credentials invalides : {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "Authentification échouée - Vérifiez vos identifiants");
        body.put("path", "tutorial-service");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Gestion des tokens invalides (401)
     */
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public ResponseEntity<Object> handleJwtException(io.jsonwebtoken.JwtException ex) {
        log.warn(" Token JWT invalide : {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "Token JWT invalide ou expiré");
        body.put("path", "tutorial-service");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}