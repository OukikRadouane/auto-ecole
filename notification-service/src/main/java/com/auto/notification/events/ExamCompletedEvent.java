package com.auto.notification.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamCompletedEvent implements Serializable {

    private String eventId;
    private String eventType;

    // ─── INFOS UTILISATEUR ───
    private String userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    // ─── INFOS EXAMEN ───
    private String examId;
    private String seriesId;
    private String seriesTheme;
    private Integer score;
    private Boolean passed;
    private Integer correctCount;
    private Integer totalQuestions;

    private LocalDateTime timestamp;
    private String source;
}