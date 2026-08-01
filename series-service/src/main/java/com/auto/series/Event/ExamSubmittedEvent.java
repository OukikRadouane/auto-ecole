package com.auto.series.Event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmittedEvent {
    private String examId;
    private String userId;
    private String examType;
    private int totalQuestions;
    private int correctCount;
    private double score;
    private boolean passed;
    private List<AnsweredQuestion> answers;
    private LocalDateTime occurredAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnsweredQuestion {
        private String questionId;
        private String serieId;
        private boolean correct;
    }
}
