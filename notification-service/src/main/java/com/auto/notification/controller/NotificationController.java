package com.auto.notification.controller;

import com.auto.notification.dto.request.EmailRequest;
import com.auto.notification.dto.response.NotificationResponse;
import com.auto.notification.entity.NotificationLog;
import com.auto.notification.security.UserPrincipal;
import com.auto.notification.service.EmailService;
import com.auto.notification.service.NotificationLogService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "API de notifications")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    private final EmailService emailService;
    private final NotificationLogService logService;

    @Operation(summary = "Envoyer un email simple")
    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> sendEmail(
            @Valid @RequestBody EmailRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = (userPrincipal != null) ? userPrincipal.getId() : "zzzz";

        try {
            emailService.sendHtmlEmail(request.getTo(), request.getSubject(), request.getBody());


            String logId = UUID.randomUUID().toString();
            logService.logSuccess(
                    //userPrincipal.getId(),
                    userId,
                    "EMAIL",
                    "manual",
                    request.getSubject(),
                    request.getBody(),
                    request.getTo()
            );

            return ResponseEntity.ok(NotificationResponse.builder()
                    .logId(UUID.fromString(logId))
                    .status("SENT")
                    .message("Email envoyé avec succès")
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(NotificationResponse.builder()
                    .status("FAILED")
                    .message("Erreur lors de l'envoi: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

    @Operation(summary = "Récupérer les logs de notification de l'utilisateur")
    @GetMapping("/logs")
    public ResponseEntity<Page<NotificationLog>> getLogs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(logService.getLogsByUser(userPrincipal.getId(), pageable));
    }

    @Operation(summary = "Récupérer les logs de notification par statut")
    @GetMapping("/logs/status/{status}")
    public ResponseEntity<Page<NotificationLog>> getLogsByStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // Filtrer par statut
        return ResponseEntity.ok(logService.getLogsByUser(userPrincipal.getId(), pageable));
    }
}