package com.auto.registration.service;

import com.auto.registration.dto.request.RegistrationRequest;
import com.auto.registration.dto.request.RegistrationStatusRequest;
import com.auto.registration.dto.response.RegistrationResponse;
import com.auto.registration.dto.response.RegistrationSummaryResponse;
import com.auto.registration.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RegistrationService {

    // ─── PUBLIC ───
    RegistrationResponse createRegistration(RegistrationRequest request);
    RegistrationResponse getRegistration(UUID registrationId, UUID userId);
    RegistrationResponse getRegistrationByUser(UUID userId);

    // ─── ADMIN ───
    Page<RegistrationSummaryResponse> getAllRegistrations(Pageable pageable, RegistrationStatus status);
    RegistrationResponse getRegistrationDetails(UUID registrationId);
    RegistrationResponse updateRegistrationStatus(UUID registrationId, RegistrationStatusRequest request, UUID adminId);
    void deleteRegistration(UUID registrationId);

    // ─── EXPORT ───
    byte[] exportRegistrationsToExcel();
}