package com.auto.auth.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
@Data
public class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "successful")
    private boolean successful;

    @Column(name = "attempt_time")
    private LocalDateTime attemptTime;

    @Column(name = "user_agent")
    private String userAgent;
}
