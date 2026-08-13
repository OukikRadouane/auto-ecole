package com.auto.registration.controller;

import com.auto.registration.dto.request.DocumentUploadRequest;
import com.auto.registration.dto.request.RegistrationRequest;
import com.auto.registration.dto.response.RegistrationResponse;
import com.auto.registration.security.UserPrincipal;
import com.auto.registration.service.DocumentService;
import com.auto.registration.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
@Tag(name = "Registration API", description = "API d'inscription (élève)")
@SecurityRequirement(name = "Bearer Authentication")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final DocumentService documentService;

    @Operation(summary = "Soumettre une demande d'inscription")
    @PostMapping
    public ResponseEntity<RegistrationResponse> createRegistration(
            @Valid @RequestBody RegistrationRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        // Vérifier que l'utilisateur crée sa propre demande
        if (!request.getUserId().equals(UUID.fromString(userPrincipal.getId()))) {
            throw new SecurityException("Vous ne pouvez pas créer une demande pour un autre utilisateur");
        }

        return ResponseEntity.ok(registrationService.createRegistration(request));
    }

    @Operation(summary = "Récupérer sa demande d'inscription")
    @GetMapping("/{registrationId}")
    public ResponseEntity<RegistrationResponse> getRegistration(
            @PathVariable UUID registrationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID userId = UUID.fromString(userPrincipal.getId());
        return ResponseEntity.ok(registrationService.getRegistration(registrationId, userId));
    }

    @Operation(summary = "Récupérer sa demande d'inscription (par utilisateur)")
    @GetMapping("/me")
    public ResponseEntity<RegistrationResponse> getMyRegistration(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID userId = UUID.fromString(userPrincipal.getId());
        return ResponseEntity.ok(registrationService.getRegistrationByUser(userId));
    }

    @Operation(summary = "Uploader un document")
    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RegistrationResponse> uploadDocument(
            @Valid @ModelAttribute DocumentUploadRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        UUID userId = UUID.fromString(userPrincipal.getId());
        return ResponseEntity.ok(documentService.uploadDocument(request, userId));
    }
}