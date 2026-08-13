package com.auto.registration.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRejectedEvent implements Serializable {

    private String eventId;
    private String eventType;
    private UUID registrationId;
    private UUID userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String reason;
    private LocalDateTime timestamp;
    private String source;
}