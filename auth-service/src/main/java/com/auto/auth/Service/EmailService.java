package com.auto.auth.Service;

import org.springframework.stereotype.Service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String token);
    void sendPasswordResetEmail(String toEmail, String token);
}
