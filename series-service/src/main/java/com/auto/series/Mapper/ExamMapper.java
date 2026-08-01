package com.auto.series.Mapper;

import com.auto.series.Dto.Response.CorrectionResponse;
import com.auto.series.Dto.Response.ExamQuestionResponse;
import com.auto.series.Dto.Response.ExamResponse;
import com.auto.series.Dto.Response.ExamResultResponse;
import com.auto.series.Entity.Exam;
import com.auto.series.Entity.ExamQuestion;
import com.auto.series.Entity.ExamResult;
import com.auto.series.Entity.Question;
import com.auto.series.Entity.UserAnswer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExamMapper {

    public ExamQuestionResponse toExamQuestionResponse(ExamQuestion examQuestion) {
        return ExamQuestionResponse.builder()
                .id(examQuestion.getId())
                .questionText(examQuestion.getQuestionText())
                .imageUrl(examQuestion.getImageUrl())
                .questionType(examQuestion.getQuestionType())
                .options(examQuestion.getOptions())
                .orderIndex(examQuestion.getOrderIndex())
                .build();
    }

    public ExamResponse toResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .examType(exam.getExamType())
                .status(exam.getStatus())
                .durationSeconds(exam.getDurationSeconds())
                .totalQuestions(exam.getTotalQuestions())
                .questions(exam.getExamQuestions().stream()
                        .map(this::toExamQuestionResponse)
                        .toList())
                .startedAt(exam.getStartedAt())
                .submittedAt(exam.getSubmittedAt())
                .build();
    }

    public ExamQuestion toExamQuestionSnapshot(Question question, String examId, int orderIndex) {
        return ExamQuestion.builder()
                .examId(examId)
                .questionId(question.getId())
                .serieId(question.getSerieId())
                .orderIndex(orderIndex)
                .questionText(question.getText())
                .imageUrl(question.getImageUrl())
                .questionType(question.getQuestionType())
                .options(question.getOptions())
                .correctAnswerIndices(question.getCorrectAnswerIndices())
                .explanation(question.getExplanation())
                .tutorialLink(question.getTutorialLink())
                .build();
    }

    public CorrectionResponse toCorrectionResponse(
            ExamResult result,
            List<ExamQuestion> examQuestions,
            List<UserAnswer> userAnswers
    ) {
        Map<String, UserAnswer> answersByExamQuestionId = userAnswers.stream()
                .collect(Collectors.toMap(UserAnswer::getExamQuestionId, a -> a));

        List<CorrectionResponse.QuestionCorrection> corrections = examQuestions.stream()
                .map(eq -> {
                    UserAnswer answer = answersByExamQuestionId.get(eq.getId());
                    return CorrectionResponse.QuestionCorrection.builder()
                            .examQuestionId(eq.getId())
                            .questionText(eq.getQuestionText())
                            .options(eq.getOptions())
                            .selectedAnswerIndices(answer != null ? answer.getSelectedAnswerIndices() : List.of())
                            .correctAnswerIndices(eq.getCorrectAnswerIndices())
                            .correct(answer != null && answer.isCorrect())
                            .explanation(eq.getExplanation())
                            .tutorialLink(eq.getTutorialLink())
                            .build();
                })
                .toList();

        return CorrectionResponse.builder()
                .examId(result.getExamId())
                .score(result.getScore())
                .passed(result.isPassed())
                .correctCount(result.getCorrectCount())
                .wrongCount(result.getWrongCount())
                .unansweredCount(result.getUnansweredCount())
                .corrections(corrections)
                .build();
    }

    public ExamResultResponse toResultResponse(ExamResult result) {
        return ExamResultResponse.builder()
                .examId(result.getExamId())
                .score(result.getScore())
                .passed(result.isPassed())
                .correctCount(result.getCorrectCount())
                .wrongCount(result.getWrongCount())
                .unansweredCount(result.getUnansweredCount())
                .totalQuestions(result.getTotalQuestions())
                .durationSeconds(result.getDurationSeconds())
                .completedAt(result.getCompletedAt())
                .build();
    }
}
