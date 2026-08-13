package com.auto.registration.entity;

import com.auto.registration.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "registrations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    private String city;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Column(name = "has_identity_card")
    @Builder.Default
    private Boolean hasIdentityCard = false;

    @Column(name = "has_photo")
    @Builder.Default
    private Boolean hasPhoto = false;

    @Column(name = "has_proof_of_address")
    @Builder.Default
    private Boolean hasProofOfAddress = false;

    @Column(name = "has_medical_certificate")
    @Builder.Default
    private Boolean hasMedicalCertificate = false;

    @Column(name = "has_all_documents")
    @Builder.Default
    private Boolean hasAllDocuments = false;

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "processed_by")
    private UUID processedBy;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "rejected_reason", columnDefinition = "TEXT")
    private String rejectedReason;

    @OneToMany(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RegistrationDocument> documents = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addDocument(RegistrationDocument document) {
        documents.add(document);
        document.setRegistration(this);
        updateDocumentStatus();
    }

    public void updateDocumentStatus() {
        boolean allPresent = Boolean.TRUE.equals(hasIdentityCard)
                && Boolean.TRUE.equals(hasPhoto)
                && Boolean.TRUE.equals(hasProofOfAddress)
                && Boolean.TRUE.equals(hasMedicalCertificate);
        this.hasAllDocuments = allPresent;
    }

    public boolean hasAllRequiredDocuments() {
        return Boolean.TRUE.equals(hasAllDocuments);
    }
}