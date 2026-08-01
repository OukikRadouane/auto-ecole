package com.auto.series.Dto.Response;

import com.auto.series.Enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QuestionResponse {
    private String id;
    private String text;
    private String imageUrl;
    private QuestionType questionType;
    private List<String> options;
    private List<Integer> correctAnswerIndices; // absent/masqué en mode examen, voir ExamQuestionResponse
    private String explanation;
    private String tutorialLink;
    private int orderIndex;
}
