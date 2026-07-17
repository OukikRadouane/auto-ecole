package com.auto.auth.Service.impl;

import com.auto.auth.Entity.RefreshToken;
import com.auto.auth.Exception.InvalidTokenException;
import com.auto.auth.Repository.RefreshTokenRepo;
import com.auto.auth.Security.JwtService;
import com.auto.auth.Service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepo refreshTokenRepo;
    private final JwtService jwtService;


    @Override
    @Transactional
    public RefreshToken createRefreshToken(String userId) {
        String tokenValue = jwtService.generateRefreshTokenValue();

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(tokenValue)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        return refreshTokenRepo.save(refreshToken);
    }

    @Override
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token invalide"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token révoqué");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expiré");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepo.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepo.save(refreshToken);
        });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(String userId) {
        refreshTokenRepo.revokeAllByUserId(userId);
    }
}
