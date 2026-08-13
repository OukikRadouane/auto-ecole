package com.auto.registration.service;

import com.auto.registration.dto.request.DocumentUploadRequest;
import com.auto.registration.dto.response.RegistrationResponse;
import com.auto.registration.entity.Registration;
import com.auto.registration.entity.RegistrationDocument;
import com.auto.registration.enums.DocumentType;
import com.auto.registration.exception.InvalidDocumentException;
import com.auto.registration.exception.ResourceNotFoundException;
import com.auto.registration.mapper.RegistrationMapper;
import com.auto.registration.repository.RegistrationDocumentRepository;
import com.auto.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {

    private final RegistrationRepository registrationRepository;
    private final RegistrationDocumentRepository documentRepository;
    private final StorageService storageService;
    private final RegistrationMapper registrationMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/png", "application/pdf"};

    public RegistrationResponse uploadDocument(DocumentUploadRequest request, UUID userId) {
        log.info("📤 Upload du document {} pour la demande {}", request.getDocumentType(), request.getRegistrationId());

        // 1. Vérifier la demande
        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée"));

        if (!registration.getUserId().equals(userId)) {
            throw new SecurityException("Vous n'avez pas accès à cette demande");
        }

        if (registration.getStatus() != com.auto.registration.enums.RegistrationStatus.PENDING) {
            throw new IllegalStateException("La demande n'est plus en attente");
        }

        // 2. Vérifier le fichier
        MultipartFile file = request.getFile();
        validateFile(file);

        // 3. Upload vers MinIO
        String fileKey = storageService.uploadFile(file, "registrations/" + registration.getId());
        String fileUrl = storageService.getFileUrl(fileKey);

        // 4. Sauvegarder en base
        RegistrationDocument document = RegistrationDocument.builder()
                .registration(registration)
                .documentType(request.getDocumentType())
                .fileName(file.getOriginalFilename())
                .fileKey(fileKey)
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .fileType(file.getContentType())
                .uploadedBy(userId)
                .build();

        documentRepository.save(document);

        // 5. Mettre à jour les flags
        updateDocumentFlag(registration, request.getDocumentType());

        log.info("✅ Document uploadé avec succès");
        return registrationMapper.toResponse(registration);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidDocumentException("Le fichier est vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidDocumentException("Le fichier dépasse la taille maximale de 5 MB");
        }

        String contentType = file.getContentType();
        boolean isValid = false;
        for (String allowedType : ALLOWED_TYPES) {
            if (allowedType.equals(contentType)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new InvalidDocumentException("Type de fichier non autorisé. Formats acceptés: JPG, PNG, PDF");
        }
    }

    private void updateDocumentFlag(Registration registration, DocumentType documentType) {
        switch (documentType) {
            case IDENTITY_CARD -> registration.setHasIdentityCard(true);
            case PHOTO -> registration.setHasPhoto(true);
            case PROOF_OF_ADDRESS -> registration.setHasProofOfAddress(true);
            case MEDICAL_CERTIFICATE -> registration.setHasMedicalCertificate(true);
        }
        registration.updateDocumentStatus();
        registrationRepository.save(registration);
    }
}