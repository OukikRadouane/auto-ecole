package com.auto.series.Service.impl;

import com.auto.series.Dto.Request.QuestionRequest;
import com.auto.series.Dto.Request.ReorderQuestionsRequest;
import com.auto.series.Dto.Response.QuestionResponse;
import com.auto.series.Entity.Question;
import com.auto.series.Entity.Serie;
import com.auto.series.Enums.SerieStatus;
import com.auto.series.Exception.InvalidQuestionException;
import com.auto.series.Exception.QuestionNotFoundException;
import com.auto.series.Exception.SerieNotFoundException;
import com.auto.series.Mapper.QuestionMapper;
import com.auto.series.Repository.QuestionRepository;
import com.auto.series.Repository.SerieRepository;
import com.auto.series.Service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{

    private final QuestionRepository questionRepository;
    private final SerieRepository serieRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public QuestionResponse addQuestion(String serieId, QuestionRequest request) {
        ensureSerieExists(serieId);
        validateQuestion(request);

        int nextOrderIndex = (int) questionRepository.countBySerieId(serieId);
        Question question = questionMapper.toEntity(request, serieId, nextOrderIndex);
        Question saved = questionRepository.save(question);
        return questionMapper.toResponse(saved);
    }

    @Override
    public List<QuestionResponse> findBySerie(String serieId) {
        ensureSerieExists(serieId);
        return questionRepository.findBySerieIdOrderByOrderIndexAsc(serieId).stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    public QuestionResponse findById(String serieId, String questionId) {
        Question question = getOrThrow(serieId, questionId);
        return questionMapper.toResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse update(String serieId, String questionId, QuestionRequest request) {
        Question question = getOrThrow(serieId, questionId);
        validateQuestion(request);
        questionMapper.updateEntity(question, request);
        Question saved = questionRepository.save(question);
        return questionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String serieId, String questionId) {
        Question question = getOrThrow(serieId, questionId);
        questionRepository.delete(question);
        reindexAfterDeletion(serieId, question.getOrderIndex());
    }

    @Override
    @Transactional
    public void reorder(String serieId, ReorderQuestionsRequest request) {
        List<Question> existing = questionRepository.findBySerieIdOrderByOrderIndexAsc(serieId);

        if (existing.size() != request.getQuestionIdsInOrder().size()) {
            throw new InvalidQuestionException(
                    "La liste de réordonnancement doit contenir exactement toutes les questions de la série");
        }

        Map<String, Question> byId = new HashMap<>();
        for (Question q : existing) {
            byId.put(q.getId(), q);
        }

        int index = 0;
        for (String questionId : request.getQuestionIdsInOrder()) {
            Question question = byId.get(questionId);
            if (question == null) {
                throw new InvalidQuestionException(
                        "La question " + questionId + " n'appartient pas à cette série");
            }
            question.setOrderIndex(index);
            index++;
        }

        questionRepository.saveAll(existing);
    }

    // ===== Helpers =====

    private void ensureSerieExists(String serieId) {
        Serie serie = serieRepository.findByIdAndStatusNot(serieId, SerieStatus.DELETED)
                .orElseThrow(() -> new SerieNotFoundException("Série introuvable : " + serieId));
    }

    private Question getOrThrow(String serieId, String questionId) {
        return questionRepository.findByIdAndSerieId(questionId, serieId)
                .orElseThrow(() -> new QuestionNotFoundException(
                        "Question introuvable : " + questionId + " dans la série " + serieId));
    }

    private void validateQuestion(QuestionRequest request) {
        try {
            request.validateConsistency();
        } catch (IllegalArgumentException e) {
            throw new InvalidQuestionException(e.getMessage());
        }
    }

    private void reindexAfterDeletion(String serieId, int deletedOrderIndex) {
        List<Question> remaining = questionRepository.findBySerieIdOrderByOrderIndexAsc(serieId);
        for (Question q : remaining) {
            if (q.getOrderIndex() > deletedOrderIndex) {
                q.setOrderIndex(q.getOrderIndex() - 1);
            }
        }
        questionRepository.saveAll(remaining);
    }
}
