package com.auto.registration.dto.request;

import com.auto.registration.enums.RegistrationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStatusRequest {

    @NotNull(message = "Le statut est obligatoire")
    private RegistrationStatus status;

    private String comment;

    private String reason;
}