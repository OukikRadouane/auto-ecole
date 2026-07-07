package com.auto.auth.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
@Data
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "jwt_token", length = 500)
    private String jwtToken;

    @Column(name = "session_id", unique = true)
    private String sessionId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType; // BROWSER, MOBILE, TABLET

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
