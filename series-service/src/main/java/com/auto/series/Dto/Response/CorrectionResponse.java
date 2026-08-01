package com.auto.series.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CorrectionResponse {
    private String examId;
    private double score;
    private boolean passed;
    private int correctCount;
    private int wrongCount;
    private int unansweredCount;
    private List<QuestionCorrection> corrections;

    @Data
    @Builder
    @AllArgsConstructor
    public static class QuestionCorrection {
        private String examQuestionId;
        private String questionText;
        private List<String> options;
        private List<Integer> selectedAnswerIndices;
        private List<Integer> correctAnswerIndices;
        private boolean correct;
        private String explanation;
        private String tutorialLink;
    }
}
