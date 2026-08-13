package com.auto.notification.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneEvent implements Serializable {

    private String eventId;
    private String eventType;

    // ─── INFOS UTILISATEUR ───
    private String userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    // ─── INFOS MILESTONE ───
    private Integer milestone; // 25, 50, 75, 100
    private Double progress;
    private String message;

    private LocalDateTime timestamp;
    private String source;
}