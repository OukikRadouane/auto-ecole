package com.auto.series.Service.impl;

import com.auto.series.Dto.Request.ExamSubmitRequest;
import com.auto.series.Dto.Request.RandomExamRequest;
import com.auto.series.Dto.Response.CorrectionResponse;
import com.auto.series.Dto.Response.ExamResponse;
import com.auto.series.Dto.Response.ExamResultResponse;
import com.auto.series.Entity.*;
import com.auto.series.Enums.ExamStatus;
import com.auto.series.Enums.ExamType;
import com.auto.series.Enums.SerieStatus;
import com.auto.series.Event.ExamSubmittedEvent;
import com.auto.series.Exception.*;
import com.auto.series.Mapper.ExamMapper;
import com.auto.series.Repository.*;
import com.auto.series.Service.ExamService;

import com.auto.series.Service.SerieEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private static final int BLANC_QUESTION_COUNT = 40;
    private static final int BLANC_DURATION_SECONDS = 1200; // 20 min
    private static final int BLANC_MAX_ERRORS = 8;  // >= 35/40 pour réussir
    static final int SECONDS_PER_QUESTION = BLANC_DURATION_SECONDS / BLANC_QUESTION_COUNT;
    private static final double DEFAULT_PASS_THRESHOLD = 80.0; // training/random : pas de règle officielle

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ExamResultRepository examResultRepository;
    private final QuestionRepository questionRepository;
    private final SerieRepository serieRepository;
    private final ExamMapper examMapper;
    private final SerieEventProducer serieEventProducer;

    @Override
    @Transactional
    public ExamResponse startExam(String serieId, String userId) {
        List<Question> pool = getActiveSerieQuestions(serieId);
        List<Question> selected = pickRandom(pool, BLANC_QUESTION_COUNT);

        Exam exam = buildAndPersistExam(selected, userId, ExamType.BLANC, BLANC_DURATION_SECONDS);
        return examMapper.toResponse(exam);
    }

    @Override
    @Transactional
    public ExamResponse startTraining(String serieId, String userId) {
        List<Question> pool = getActiveSerieQuestions(serieId);
        // ordre pédagogique conservé, pas de mélange, pas de chrono
        Exam exam = buildAndPersistExam(pool, userId, ExamType.TRAINING, null);
        return examMapper.toResponse(exam);
    }

    @Override
    @Transactional
    public ExamResponse startRandomExam(RandomExamRequest request, String userId) {
        List<Question> pool = questionRepository.findAllBySerieIdIn(request.getSeriesIds());
        if (pool.isEmpty()) {
            throw new InvalidQuestionException("Aucune question disponible dans les séries données");
        }

        int count = Math.min(request.getQuestionCount(), pool.size());
        List<Question> selected = pickRandom(pool, count);
        int duration = count * SECONDS_PER_QUESTION;

        Exam exam = buildAndPersistExam(selected, userId, ExamType.RANDOM, duration);
        return examMapper.toResponse(exam);
    }

    @Override
    public ExamResponse getExam(String examId, String userId) {
        Exam exam = getOwnedOrThrow(examId, userId);
        return examMapper.toResponse(exam);
    }

    @Override
    @Transactional
    public ExamResultResponse submitExam(String examId, ExamSubmitRequest request, String userId) {
        Exam exam = getOwnedOrThrow(examId, userId);

        if (exam.getStatus() != ExamStatus.IN_PROGRESS) {
            throw new ExamAlreadySubmittedException("Cet examen a déjà été soumis ou a expiré");
        }
        if (exam.isExpired()) {
            exam.setStatus(ExamStatus.EXPIRED);
            examRepository.save(exam);
            throw new ExamExpiredException("Le temps imparti pour cet examen est écoulé");
        }

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByOrderIndexAsc(examId);

        Map<String, List<Integer>> submittedByExamQuestionId = new HashMap<>();
        for (ExamSubmitRequest.AnswerSubmission a : request.getAnswers()) {
            submittedByExamQuestionId.put(a.getExamQuestionId(),
                    a.getSelectedAnswerIndices() != null ? a.getSelectedAnswerIndices() : List.of());
        }

        int correctCount = 0, wrongCount = 0, unansweredCount = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();

        for (ExamQuestion eq : examQuestions) {
            List<Integer> selected = submittedByExamQuestionId.getOrDefault(eq.getId(), List.of());
            boolean answered = !selected.isEmpty();
            boolean correct = answered && new HashSet<>(selected).equals(new HashSet<>(eq.getCorrectAnswerIndices()));

            if (!answered) unansweredCount++;
            else if (correct) correctCount++;
            else wrongCount++;

            userAnswers.add(UserAnswer.builder()
                    .examId(examId)
                    .examQuestionId(eq.getId())
                    .questionId(eq.getQuestionId())
                    .userId(userId)
                    .selectedAnswerIndices(selected)
                    .correct(correct)
                    .answeredAt(answered ? LocalDateTime.now() : null)
                    .build());
        }
        userAnswerRepository.saveAll(userAnswers);

        double score = examQuestions.isEmpty() ? 0.0 : (correctCount * 100.0 / examQuestions.size());
        boolean passed = computePassed(exam.getExamType(), correctCount, wrongCount, unansweredCount, score);
        int actualDuration = (int) Duration.between(exam.getStartedAt(), LocalDateTime.now()).getSeconds();

        ExamResult result = ExamResult.builder()
                .examId(examId)
                .userId(userId)
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .unansweredCount(unansweredCount)
                .totalQuestions(examQuestions.size())
                .score(score)
                .passed(passed)
                .durationSeconds(actualDuration)
                .build();
        ExamResult savedResult = examResultRepository.save(result);

        exam.setStatus(ExamStatus.SUBMITTED);
        exam.setSubmittedAt(LocalDateTime.now());
        examRepository.save(exam);

        List<ExamSubmittedEvent.AnsweredQuestion> answeredQuestions = new ArrayList<>();

        for (int i = 0; i < examQuestions.size(); i++) {
            ExamQuestion eq = examQuestions.get(i);
            answeredQuestions.add(ExamSubmittedEvent.AnsweredQuestion.builder()
                    .questionId(eq.getQuestionId())
                    .serieId(eq.getSerieId())
                    .correct(userAnswers.get(i).isCorrect())
                    .build());
        }

        serieEventProducer.publishExamSubmitted(
                examId, userId, exam.getExamType().name(),
                examQuestions.size(), correctCount, score, passed,
                answeredQuestions
        );

        return examMapper.toResultResponse(savedResult);
    }

    @Override
    public CorrectionResponse getCorrection(String examId, String userId) {
        Exam exam = getOwnedOrThrow(examId, userId);
        if (exam.getStatus() != ExamStatus.SUBMITTED) {
            throw new ExamAlreadySubmittedException("Cet examen n'a pas encore été soumis");
        }

        ExamResult result = examResultRepository.findByExamId(examId)
                .orElseThrow(() -> new ExamNotFoundException("Résultat introuvable pour cet examen"));
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByOrderIndexAsc(examId);
        List<UserAnswer> userAnswers = userAnswerRepository.findByExamId(examId);

        return examMapper.toCorrectionResponse(result, examQuestions, userAnswers);
    }

    @Override
    public ExamResponse resumeExam(String userId) {
        Exam exam = examRepository.findFirstByUserIdAndStatusOrderByStartedAtDesc(userId, ExamStatus.IN_PROGRESS)
                .orElseThrow(() -> new ExamNotFoundException("Aucun examen en cours à reprendre"));

        if (exam.isExpired()) {
            exam.setStatus(ExamStatus.EXPIRED);
            examRepository.save(exam);
            throw new ExamExpiredException("L'examen précédent a expiré, veuillez en démarrer un nouveau");
        }

        return examMapper.toResponse(exam);
    }

    // ===== Helpers =====
    private List<Question> getActiveSerieQuestions(String serieId) {
        Serie serie = serieRepository.findByIdAndStatusNot(serieId, SerieStatus.DELETED)
                .orElseThrow(() -> new SerieNotFoundException("Série introuvable : " + serieId));

        List<Question> questions = questionRepository.findBySerieIdOrderByOrderIndexAsc(serieId);
        if (questions.isEmpty()) {
            throw new InvalidQuestionException("Cette série ne contient aucune question");
        }
        return questions;
    }

    private List<Question> pickRandom(List<Question> pool, int count) {
        List<Question> copy = new ArrayList<>(pool);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(count, copy.size()));
    }

    private Exam buildAndPersistExam(List<Question> questions, String userId, ExamType type, Integer durationSeconds) {
        Exam exam = Exam.builder()
                .userId(userId)
                .examType(type)
                .status(ExamStatus.IN_PROGRESS)
                .durationSeconds(durationSeconds)
                .totalQuestions(questions.size())
                .build();
        Exam savedExam = examRepository.save(exam);

        List<ExamQuestion> examQuestions = new ArrayList<>();
        int index = 0;
        for (Question q : questions) {
            examQuestions.add(examMapper.toExamQuestionSnapshot(q, savedExam.getId(), index));
            index++;
        }
        examQuestionRepository.saveAll(examQuestions);
        savedExam.setExamQuestions(examQuestions);
        return savedExam;
    }

    private Exam getOwnedOrThrow(String examId, String userId) {
        // findByIdAndUserId : ne distingue pas "n'existe pas" de "appartient à un autre utilisateur"
        // volontairement, pour ne jamais révéler l'existence d'un examen d'un autre élève
        return examRepository.findByIdAndUserId(examId, userId)
                .orElseThrow(() -> new ExamNotFoundException("Examen introuvable : " + examId));
    }

    private boolean computePassed(ExamType type, int correct, int wrong, int unanswered, double score) {
        if (type == ExamType.BLANC) {
            // règle du vrai code de la route : max 5 erreurs (fautes + non-réponses) sur 40
            return (wrong + unanswered) <= BLANC_MAX_ERRORS;
        }
        return score >= DEFAULT_PASS_THRESHOLD;
    }
}
