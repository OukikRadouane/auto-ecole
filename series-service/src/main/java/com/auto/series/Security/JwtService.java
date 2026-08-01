package com.auto.series.Security;

public interface JwtService {
    boolean isTokenValid(String token);
    String extractUserId(String token);
    String extractRole(String token);
}
