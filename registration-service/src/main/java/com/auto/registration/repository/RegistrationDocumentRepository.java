package com.auto.registration.repository;

import com.auto.registration.entity.RegistrationDocument;
import com.auto.registration.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationDocumentRepository extends JpaRepository<RegistrationDocument, UUID> {

    List<RegistrationDocument> findByRegistrationId(UUID registrationId);

    Optional<RegistrationDocument> findByRegistrationIdAndDocumentType(UUID registrationId, DocumentType documentType);

    boolean existsByRegistrationIdAndDocumentType(UUID registrationId, DocumentType documentType);

    void deleteByRegistrationId(UUID registrationId);
}