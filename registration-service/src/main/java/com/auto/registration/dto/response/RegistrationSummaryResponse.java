package com.auto.registration.dto.response;

import com.auto.registration.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationSummaryResponse {

    private UUID id;
    private UUID userId;
    private String userEmail;
    private String firstName;
    private String lastName;
    private RegistrationStatus status;
    private Boolean hasAllDocuments;
    private LocalDateTime createdAt;
}