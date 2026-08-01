package com.auto.series.Dto.Request;

import com.auto.series.Enums.QuestionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {

    @NotBlank
    @Size(max = 2000)
    private String text;

    private String imageUrl;

    private String audioUrl;

    @NotNull
    private QuestionType questionType;

    @NotEmpty
    @Size(min = 2, max = 6, message = "Une question doit avoir entre 2 et 6 options")
    private List<@NotBlank String> options;

    @NotEmpty(message = "Au moins une réponse correcte doit être indiquée")
    private List<@NotNull @Min(0) Integer> correctAnswerIndices;

    @Size(max = 2000)
    private String explanation;

    private String tutorialLink;

    public void validateConsistency() {
        if (questionType == QuestionType.SINGLE_CHOICE && correctAnswerIndices.size() != 1) {
            throw new IllegalArgumentException(
                    "Une question à choix unique doit avoir exactement une réponse correcte");
        }
        if (questionType == QuestionType.MULTIPLE_CHOICE && correctAnswerIndices.size() < 2) {
            throw new IllegalArgumentException(
                    "Une question à choix multiple doit avoir au moins deux réponses correctes");
        }
        int optionCount = options.size();
        boolean outOfBounds = correctAnswerIndices.stream().anyMatch(i -> i >= optionCount);
        if (outOfBounds) {
            throw new IllegalArgumentException(
                    "Un index de réponse correcte dépasse le nombre d'options disponibles");
        }
        if (new java.util.HashSet<>(correctAnswerIndices).size() != correctAnswerIndices.size()) {
            throw new IllegalArgumentException("Les indices de réponses correctes doivent être uniques");
        }
    }
}
