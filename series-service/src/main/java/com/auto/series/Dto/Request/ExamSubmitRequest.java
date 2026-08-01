package com.auto.series.Dto.Request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ExamSubmitRequest {
    @NotEmpty
    @Valid
    private List<AnswerSubmission> answers;

    @Data
    public static class AnswerSubmission {
        private String examQuestionId;
        private List<Integer> selectedAnswerIndices; // vide/absent = pas de réponse donnée
    }
}
