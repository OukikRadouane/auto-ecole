package com.auto.series.Repository;

import com.auto.series.Entity.Exam;
import com.auto.series.Enums.ExamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, String> {

    // GET /api/exam/{examId} — vérifie que l'examen appartient bien à l'utilisateur
    Optional<Exam> findByIdAndUserId(String examId, String userId);

    // pour "reprendre un examen interrompu"
    Optional<Exam> findFirstByUserIdAndStatusOrderByStartedAtDesc(String userId, ExamStatus status);

    List<Exam> findByUserIdOrderByStartedAtDesc(String userId);

    Page<Exam> findByUserId(String userId, Pageable pageable);

    // pour un job de nettoyage périodique des examens expirés non soumis
    List<Exam> findByStatusAndStartedAtBefore(ExamStatus status, java.time.LocalDateTime cutoff);
}
