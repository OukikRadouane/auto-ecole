package com.auto.auth.Repository;

import com.auto.auth.Entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepo extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByToken(String token);
}
