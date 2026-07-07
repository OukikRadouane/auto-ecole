package com.auto.auth.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "action", nullable = false, length = 100)
    private String action; // LOGIN, LOGOUT, REGISTER, UPDATE_USER, DELETE_USER, etc.

    @Column(name = "target")
    private String target; // ID de l'utilisateur ciblé si applicable

    @Column(name = "details", columnDefinition = "jsonb")
    private String details; // JSON avec les détails

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
