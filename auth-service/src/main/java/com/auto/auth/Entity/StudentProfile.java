package com.auto.auth.Entity;

import com.auto.auth.Enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "student_profiles")
@Data
public class StudentProfile {
    @Id
    @Column(name = "user_id")
    private String userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status")
    private RegistrationStatus registrationStatus = RegistrationStatus.PENDING;

    @Column(name = "registration_documents", columnDefinition = "jsonb")
    private String registrationDocuments;

    @Column(name = "total_series_completed")
    private int totalSeriesCompleted = 0;

    @Column(name = "average_score")
    private double averageScore = 0.0;

    @Column(name = "weak_topics", columnDefinition = "text[]")
    private List<String> weakTopics;

    @Column(name = "exam_attempts")
    private int examAttempts = 0;

    @Column(name = "last_exam_date")
    private LocalDateTime lastExamDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
