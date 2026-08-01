package com.auto.series.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ExamResultResponse {
    private String examId;
    private double score;
    private boolean passed;
    private int correctCount;
    private int wrongCount;
    private int unansweredCount;
    private int totalQuestions;
    private Integer durationSeconds;
    private LocalDateTime completedAt;
}
