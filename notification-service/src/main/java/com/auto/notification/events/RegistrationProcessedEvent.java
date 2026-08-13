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
public class RegistrationProcessedEvent implements Serializable {

    private String eventId;
    private String eventType;

    // ─── INFOS UTILISATEUR ───
    private String userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    // ─── INFOS INSCRIPTION ───
    private String registrationId;
    private String status;
    private String message;

    private LocalDateTime timestamp;
    private String source;
}