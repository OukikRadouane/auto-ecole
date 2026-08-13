package com.auto.registration.events;

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
public class UserCreatedEvent implements Serializable {

    private String eventId;
    private String eventType;
    private String userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String role;
    private LocalDateTime timestamp;
    private String source;
}