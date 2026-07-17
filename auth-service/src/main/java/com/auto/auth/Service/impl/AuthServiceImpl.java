package com.auto.auth.Service.impl;

import com.auto.auth.Dto.Request.LoginRequest;
import com.auto.auth.Dto.Request.RegisterRequest;
import com.auto.auth.Dto.Response.AuthResponse;
import com.auto.auth.Dto.Response.UserResponse;
import com.auto.auth.Entity.EmailVerificationToken;
import com.auto.auth.Entity.PasswordResetToken;
import com.auto.auth.Entity.RefreshToken;
import com.auto.auth.Entity.User;
import com.auto.auth.Enums.Role;
import com.auto.auth.Exception.EmailAlreadyUsedException;
import com.auto.auth.Exception.InvalidCredentialsException;
import com.auto.auth.Exception.InvalidTokenException;
import com.auto.auth.Exception.UserNotFoundException;
import com.auto.auth.Repository.EmailVerificationTokenRepo;
import com.auto.auth.Repository.PasswordResetTokenRepo;
import com.auto.auth.Repository.RefreshTokenRepo;
import com.auto.auth.Repository.UserRepo;
import com.auto.auth.Security.JwtService;
import com.auto.auth.Service.AuthService;
import com.auto.auth.Service.EmailService;
import com.auto.auth.Service.TokenService;
import com.auto.auth.Service.UserEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepo userRepo;
    private final EmailVerificationTokenRepo emailVerificationTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserEventProducer eventProducer;
    private final JwtService jwtService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final TokenService tokenService;

    @Override
    @Transactional
    public void register(RegisterRequest request){
        if(userRepo.existsByEmail(request.getEmail())){
            throw new EmailAlreadyUsedException("Un compte existe déjà avec cet email");
        }
        User user = User.builder()
                        .email(request.getEmail())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                        .firstName(request.getFirstName())
                                                .lastName(request.getLastName())
                                                        .phone(request.getPhone())
                                                                .role(Role.STUDENT)
                                                                        .emailVerified(false)
                                                                                .enabled(true)
                                                                                        .build();

        User saved = userRepo.save(user);

        String rawToken = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .userId(saved.getId())
                .token(rawToken)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        emailVerificationTokenRepo.save(verificationToken);

        emailService.sendVerificationEmail(saved.getEmail(), rawToken);
        eventProducer.publishUserRegistered(saved.getId(), saved.getEmail());
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request){
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email ou mot de passe incorrect"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }
        if(!user.isEnabled()){
            throw new InvalidCredentialsException("Compte désactivé");
        }

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshTokenValue){
        RefreshToken storedToken = refreshTokenRepo.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token invalide"));

        if(storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("Refresh token expiré ou révoqué");
        }

        storedToken.setRevoked(true);
        refreshTokenRepo.save(storedToken);

        User user = userRepo.findById(storedToken.getUserId())
                .orElseThrow(() -> new InvalidTokenException("Utilisateur introuvable"));

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue){
        refreshTokenRepo.findByToken(refreshTokenValue)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                });
    }
    public void verifyEmail(String token){
        EmailVerificationToken verificationToken = emailVerificationTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Lien de vérification invalide"));

        if(verificationToken.isUsed() || verificationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new InvalidTokenException("Lien de vérification expiré");
        }

        User user = userRepo.findById(verificationToken.getUserId())
                .orElseThrow(() -> new InvalidTokenException("Utilisateur introuvable"));

        user.setEmailVerified(true);
        userRepo.save(user);

        verificationToken.setUsed(true);
        emailVerificationTokenRepo.save(verificationToken);

        eventProducer.publishUserEmailVerified(user.getId());
    }

    @Override
    public void forgotPassword(String email) {
        userRepo.findByEmail(email).ifPresent(user -> {
            String rawToken = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .userId(user.getId())
                    .token(rawToken)
                    .expiresAt(LocalDateTime.now().plusMinutes(30))
                    .used(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            passwordResetTokenRepo.save(resetToken);
            emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
                });
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Lien de réinitialisation invalide"));

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Lien de réinitialisation expiré");
        }
        User user = userRepo.findById(resetToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepo.save(resetToken);

        refreshTokenRepo.revokeAllByUserId(user.getId());
    }

    @Override
    public void changePassword(String userId, String currentPassword, String newPassword) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Mot de passe actuel incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        refreshTokenRepo.revokeAllByUserId(user.getId());
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = tokenService.createRefreshToken(user.getId());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .build();
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtService.getAccessTokenExpirySeconds())
                .user(userResponse)
                .build();
    }


}
