package com.auto.auth.Service;

import com.auto.auth.Dto.Request.LoginRequest;
import com.auto.auth.Dto.Request.RegisterRequest;
import com.auto.auth.Dto.Response.AuthResponse;

public interface AuthService {
    void register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String refreshToken);
    void logout(String refreshToken);
    void verifyEmail(String token);

    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
    void changePassword(String userId, String currentPassword, String newPassword);
}
