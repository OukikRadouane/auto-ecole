package com.auto.registration.dto.response;

import com.auto.registration.enums.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    private UUID id;
    private UUID userId;
    private String userEmail;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String postalCode;
    private String city;
    private LocalDate birthDate;
    private RegistrationStatus status;
    private Boolean hasIdentityCard;
    private Boolean hasPhoto;
    private Boolean hasProofOfAddress;
    private Boolean hasMedicalCertificate;
    private Boolean hasAllDocuments;
    private String adminComment;
    private String rejectedReason;
    private List<DocumentResponse> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime processedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentResponse {
        private UUID id;
        private String documentType;
        private String fileName;
        private String fileUrl;
        private Long fileSize;
        private String fileType;
        private LocalDateTime uploadedAt;
    }
}