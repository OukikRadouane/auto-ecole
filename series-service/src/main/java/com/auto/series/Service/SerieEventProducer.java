package com.auto.series.Service;

import com.auto.series.Event.ExamSubmittedEvent;

import java.util.List;

public interface SerieEventProducer {
    void publishSerieCreated(String serieId, String title, String theme, String createdBy);
    void publishSerieUpdated(String serieId, String modifiedBy);
    void publishSerieDeleted(String serieId, String deletedBy);
    void publishExamSubmitted(
            String examId, String userId, String examType,
            int totalQuestions, int correctCount, double score, boolean passed,
            List<ExamSubmittedEvent.AnsweredQuestion> answers
    );
}
