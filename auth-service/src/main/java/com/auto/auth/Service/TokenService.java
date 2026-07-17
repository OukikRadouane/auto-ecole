package com.auto.auth.Service;

import com.auto.auth.Entity.RefreshToken;

public interface TokenService {
    RefreshToken createRefreshToken(String userId);
    RefreshToken validateRefreshToken(String token);
    void revokeRefreshToken(String token);
    void revokeAllUserTokens(String userId);
}
