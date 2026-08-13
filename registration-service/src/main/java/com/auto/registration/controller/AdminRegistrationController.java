package com.auto.registration.controller;

import com.auto.registration.dto.request.RegistrationStatusRequest;
import com.auto.registration.dto.response.RegistrationResponse;
import com.auto.registration.dto.response.RegistrationSummaryResponse;
import com.auto.registration.enums.RegistrationStatus;
import com.auto.registration.security.UserPrincipal;
import com.auto.registration.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/registrations")
@RequiredArgsConstructor
@Tag(name = "Admin Registration API", description = "API d'administration des inscriptions")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRegistrationController {

    private final RegistrationService registrationService;

    @Operation(summary = "Lister toutes les demandes d'inscription")
    @GetMapping
    public ResponseEntity<Page<RegistrationSummaryResponse>> getAllRegistrations(
            @RequestParam(required = false) RegistrationStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(registrationService.getAllRegistrations(pageable, status));
    }

    @Operation(summary = "Récupérer les détails d'une demande")
    @GetMapping("/{registrationId}")
    public ResponseEntity<RegistrationResponse> getRegistrationDetails(@PathVariable UUID registrationId) {
        return ResponseEntity.ok(registrationService.getRegistrationDetails(registrationId));
    }

    @Operation(summary = "Valider ou rejeter une demande")
    @PutMapping("/{registrationId}/status")
    public ResponseEntity<RegistrationResponse> updateRegistrationStatus(
            @PathVariable UUID registrationId,
            @Valid @RequestBody RegistrationStatusRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID adminId = UUID.fromString(userPrincipal.getId());
        return ResponseEntity.ok(registrationService.updateRegistrationStatus(registrationId, request, adminId));
    }

    @Operation(summary = "Supprimer une demande (non traitée)")
    @DeleteMapping("/{registrationId}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable UUID registrationId) {
        registrationService.deleteRegistration(registrationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Exporter les demandes en Excel")
    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportRegistrations() {
        byte[] excelData = registrationService.exportRegistrationsToExcel();
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=registrations.xlsx")
                .body(excelData);
    }
}