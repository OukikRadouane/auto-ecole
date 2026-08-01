package com.auto.series.Dto.Response;

import com.auto.series.Enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ExamQuestionResponse {
    private String id; // id de l'ExamQuestion, pas de la Question source
    private String questionText;
    private String imageUrl;
    private QuestionType questionType;
    private List<String> options;
    private int orderIndex;
}
