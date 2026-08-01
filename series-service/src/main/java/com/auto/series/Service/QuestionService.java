package com.auto.series.Service;

import com.auto.series.Dto.Request.QuestionRequest;
import com.auto.series.Dto.Request.ReorderQuestionsRequest;
import com.auto.series.Dto.Response.QuestionResponse;

import java.util.List;

public interface QuestionService {
    QuestionResponse addQuestion(String serieId, QuestionRequest request);
    List<QuestionResponse> findBySerie(String serieId);
    QuestionResponse findById(String serieId, String questionId);
    QuestionResponse update(String serieId, String questionId, QuestionRequest request);
    void delete(String serieId, String questionId);
    void reorder(String serieId, ReorderQuestionsRequest request);
}
