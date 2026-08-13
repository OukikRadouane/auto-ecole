package com.auto.registration.service;

import com.auto.registration.dto.request.RegistrationRequest;
import com.auto.registration.dto.request.RegistrationStatusRequest;
import com.auto.registration.dto.response.RegistrationResponse;
import com.auto.registration.dto.response.RegistrationSummaryResponse;
import com.auto.registration.entity.Registration;
import com.auto.registration.enums.RegistrationStatus;
import com.auto.registration.exception.ResourceNotFoundException;
import com.auto.registration.mapper.RegistrationMapper;
import com.auto.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final RegistrationMapper registrationMapper;
    private final EventPublisherService eventPublisherService;

    // ─── PUBLIC ───

    @Override
    public RegistrationResponse createRegistration(RegistrationRequest request) {
        log.info("📝 Création d'une demande d'inscription pour l'utilisateur: {}", request.getUserId());

        // Vérifier si l'utilisateur a déjà une demande en cours
        List<RegistrationStatus> activeStatuses = List.of(
                RegistrationStatus.PENDING,
                RegistrationStatus.PROCESSED
        );
        if (registrationRepository.existsByUserIdAndStatusIn(request.getUserId(), activeStatuses)) {
            throw new IllegalStateException("Vous avez déjà une demande d'inscription en cours");
        }

        Registration registration = registrationMapper.toEntity(request);
        Registration saved = registrationRepository.save(registration);

        // Publier événement
        eventPublisherService.publishRegistrationSubmitted(saved);

        log.info("✅ Demande d'inscription créée: {}", saved.getId());
        return registrationMapper.toResponse(saved);
    }

    @Override
    public RegistrationResponse getRegistration(UUID registrationId, UUID userId) {
        log.debug("📋 Récupération de la demande: {}", registrationId);

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + registrationId));

        if (!registration.getUserId().equals(userId)) {
            throw new SecurityException("Vous n'avez pas accès à cette demande");
        }

        return registrationMapper.toResponse(registration);
    }

    @Override
    public RegistrationResponse getRegistrationByUser(UUID userId) {
        log.debug("📋 Récupération de la demande de l'utilisateur: {}", userId);

        Registration registration = registrationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune demande trouvée pour cet utilisateur"));

        return registrationMapper.toResponse(registration);
    }

    // ─── ADMIN ───

    @Override
    public Page<RegistrationSummaryResponse> getAllRegistrations(Pageable pageable, RegistrationStatus status) {
        log.debug("📋 Récupération des demandes (admin)");

        if (status != null) {
            return registrationRepository.findByStatus(status, pageable)
                    .map(registrationMapper::toSummaryResponse);
        }

        return registrationRepository.findAll(pageable)
                .map(registrationMapper::toSummaryResponse);
    }

    @Override
    public RegistrationResponse getRegistrationDetails(UUID registrationId) {
        log.debug("📋 Récupération des détails de la demande: {}", registrationId);

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + registrationId));

        return registrationMapper.toResponse(registration);
    }

    @Override
    public RegistrationResponse updateRegistrationStatus(UUID registrationId, RegistrationStatusRequest request, UUID adminId) {
        log.info("📝 Mise à jour du statut de la demande {}: {}", registrationId, request.getStatus());

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + registrationId));

        RegistrationStatus oldStatus = registration.getStatus();
        RegistrationStatus newStatus = request.getStatus();

        // Vérifier si la demande peut être traitée
        if (newStatus == RegistrationStatus.PROCESSED && !registration.hasAllRequiredDocuments()) {
            throw new IllegalStateException("Tous les documents ne sont pas encore uploadés");
        }

        // Mettre à jour le statut
        registration.setStatus(newStatus);
        registration.setProcessedBy(adminId);
        registration.setProcessedAt(LocalDateTime.now());

        if (newStatus == RegistrationStatus.REJECTED) {
            registration.setRejectedReason(request.getReason());
        }

        if (newStatus == RegistrationStatus.PROCESSED) {
            registration.setAdminComment(request.getComment());
        }

        Registration updated = registrationRepository.save(registration);

        // Publier l'événement correspondant
        if (newStatus == RegistrationStatus.PROCESSED) {
            eventPublisherService.publishRegistrationProcessed(updated);
        } else if (newStatus == RegistrationStatus.REJECTED) {
            eventPublisherService.publishRegistrationRejected(updated);
        }

        log.info("✅ Statut de la demande {} mis à jour: {} → {}", registrationId, oldStatus, newStatus);
        return registrationMapper.toResponse(updated);
    }

    @Override
    public void deleteRegistration(UUID registrationId) {
        log.info("🗑️ Suppression de la demande: {}", registrationId);

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvée: " + registrationId));

        // Ne pas supprimer si déjà traitée
        if (registration.getStatus() == RegistrationStatus.PROCESSED) {
            throw new IllegalStateException("Impossible de supprimer une demande déjà traitée");
        }

        registrationRepository.delete(registration);
        log.info("✅ Demande supprimée: {}", registrationId);
    }

    // ─── EXPORT ───

    @Override
    public byte[] exportRegistrationsToExcel() {
        log.info("📊 Export des demandes vers Excel");

        List<Registration> registrations = registrationRepository.findAll();
        // TODO: Implémenter l'export Excel avec Apache POI

        return new byte[0];
    }
}