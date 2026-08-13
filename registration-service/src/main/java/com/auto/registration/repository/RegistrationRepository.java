package com.auto.registration.repository;

import com.auto.registration.entity.Registration;
import com.auto.registration.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {

    Optional<Registration> findByUserId(UUID userId);

    Page<Registration> findByStatus(RegistrationStatus status, Pageable pageable);

    Page<Registration> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT r FROM Registration r WHERE r.status = :status AND r.hasAllDocuments = true")
    Page<Registration> findReadyForProcessing(@Param("status") RegistrationStatus status, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.status = :status")
    long countByStatus(@Param("status") RegistrationStatus status);

    List<Registration> findByStatusOrderByCreatedAtAsc(RegistrationStatus status);

    boolean existsByUserIdAndStatusIn(UUID userId, List<RegistrationStatus> statuses);
}