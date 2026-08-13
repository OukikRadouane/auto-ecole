package com.auto.registration.mapper;

import com.auto.registration.dto.request.RegistrationRequest;
import com.auto.registration.dto.response.RegistrationResponse;
import com.auto.registration.dto.response.RegistrationSummaryResponse;
import com.auto.registration.entity.Registration;
import com.auto.registration.entity.RegistrationDocument;
import com.auto.registration.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RegistrationMapper {

    private StorageService storageService;

    // ✅ Injection de StorageService
    @Autowired
    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    public Registration toEntity(RegistrationRequest request) {
        return Registration.builder()
                .userId(request.getUserId())
                .userEmail(request.getUserEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .postalCode(request.getPostalCode())
                .city(request.getCity())
                .birthDate(request.getBirthDate())
                .build();
    }

    public RegistrationResponse toResponse(Registration registration) {
        return RegistrationResponse.builder()
                .id(registration.getId())
                .userId(registration.getUserId())
                .userEmail(registration.getUserEmail())
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .phone(registration.getPhone())
                .address(registration.getAddress())
                .postalCode(registration.getPostalCode())
                .city(registration.getCity())
                .birthDate(registration.getBirthDate())
                .status(registration.getStatus())
                .hasIdentityCard(registration.getHasIdentityCard())
                .hasPhoto(registration.getHasPhoto())
                .hasProofOfAddress(registration.getHasProofOfAddress())
                .hasMedicalCertificate(registration.getHasMedicalCertificate())
                .hasAllDocuments(registration.getHasAllDocuments())
                .adminComment(registration.getAdminComment())
                .rejectedReason(registration.getRejectedReason())
                // ✅ Appelle toDocumentResponse qui génère une URL fraîche
                .documents(registration.getDocuments() != null && !registration.getDocuments().isEmpty() ?
                        registration.getDocuments().stream()
                                .map(this::toDocumentResponse)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .createdAt(registration.getCreatedAt())
                .updatedAt(registration.getUpdatedAt())
                .processedAt(registration.getProcessedAt())
                .build();
    }

    public RegistrationSummaryResponse toSummaryResponse(Registration registration) {
        return RegistrationSummaryResponse.builder()
                .id(registration.getId())
                .userId(registration.getUserId())
                .userEmail(registration.getUserEmail())
                .firstName(registration.getFirstName())
                .lastName(registration.getLastName())
                .status(registration.getStatus())
                .hasAllDocuments(registration.getHasAllDocuments())
                .createdAt(registration.getCreatedAt())
                .build();
    }

    // ─── DOCUMENT RESPONSE AVEC URL FRAÎCHE ───
    public RegistrationResponse.DocumentResponse toDocumentResponse(RegistrationDocument document) {
        // ✅ Génère une URL FRAÎCHE à partir du fileKey
        String freshUrl = storageService != null ?
                storageService.getFileUrl(document.getFileKey()) :
                null;

        return RegistrationResponse.DocumentResponse.builder()
                .id(document.getId())
                .documentType(document.getDocumentType().name())
                .fileName(document.getFileName())
                .fileUrl(freshUrl) // ← URL fraîche !
                .fileSize(document.getFileSize())
                .fileType(document.getFileType())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}