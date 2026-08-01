package com.auto.series.Mapper;

import com.auto.series.Dto.Request.QuestionRequest;
import com.auto.series.Dto.Response.QuestionResponse;
import com.auto.series.Entity.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .text(question.getText())
                .imageUrl(question.getImageUrl())
                .questionType(question.getQuestionType())
                .options(question.getOptions())
                .correctAnswerIndices(question.getCorrectAnswerIndices())
                .explanation(question.getExplanation())
                .tutorialLink(question.getTutorialLink())
                .orderIndex(question.getOrderIndex())
                .build();
    }

    public Question toEntity(QuestionRequest request, String serieId, int orderIndex) {
        return Question.builder()
                .seriesId(serieId)
                .text(request.getText())
                .imageUrl(request.getImageUrl())
                .questionType(request.getQuestionType())
                .options(request.getOptions())
                .correctAnswerIndices(request.getCorrectAnswerIndices())
                .explanation(request.getExplanation())
                .tutorialLink(request.getTutorialLink())
                .orderIndex(orderIndex)
                .build();
    }

    public void updateEntity(Question question, QuestionRequest request) {
        question.setText(request.getText());
        question.setImageUrl(request.getImageUrl());
        question.setQuestionType(request.getQuestionType());
        question.setOptions(request.getOptions());
        question.setCorrectAnswerIndices(request.getCorrectAnswerIndices());
        question.setExplanation(request.getExplanation());
        question.setTutorialLink(request.getTutorialLink());
        // orderIndex volontairement absent : géré uniquement par le réordonnancement dédié
    }
}
