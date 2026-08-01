package com.auto.series.Entity;

import com.auto.series.Enums.ExamStatus;
import com.auto.series.Enums.ExamType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExamStatus status = ExamStatus.IN_PROGRESS;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds; // 1800 pour un examen blanc de 30 min, null pour training

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("orderIndex ASC")
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // ===== Helpers =====

    public boolean isExpired() {
        if (status != ExamStatus.IN_PROGRESS || durationSeconds == null) return false;
        return startedAt.plusSeconds(durationSeconds).isBefore(LocalDateTime.now());
    }
}
