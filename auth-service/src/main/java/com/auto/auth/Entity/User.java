package com.auto.auth.Entity;

import com.auto.auth.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "first_name", length = 100)
    private String nom;

    @Column(name = "last_name", length = 100)
    private String prenom;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT;

    @Column(length = 2)
    private String language = "fr";

    @Column(length = 10)
    private String theme = "light";

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "phone_verified")
    private boolean phoneVerified = false;

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled = false;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "account_expired")
    private boolean accountExpired = false;

    @Column(name = "credentials_expired")
    private boolean credentialsExpired = false;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "failed_attempts")
    private int failedAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private StudentProfile studentProfile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EmailVerificationToken> emailVerificationTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AuditLog> auditLogs = new ArrayList<>();
}
