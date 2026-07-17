package com.auto.auth.Security;

import com.auto.auth.Entity.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshTokenValue();
    long getAccessTokenExpirySeconds();
    String extractUserId(String accessToken);
    boolean isTokenValid(String token);
}
