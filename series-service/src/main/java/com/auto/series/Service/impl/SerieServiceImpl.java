package com.auto.series.Service.impl;

import com.auto.series.Dto.Request.SerieFilterRequest;
import com.auto.series.Dto.Request.SerieRequest;
import com.auto.series.Dto.Response.PageResponse;
import com.auto.series.Dto.Response.SerieDetailResponse;
import com.auto.series.Dto.Response.SerieResponse;
import com.auto.series.Entity.Question;
import com.auto.series.Entity.Serie;
import com.auto.series.Entity.SeriesHistory;
import com.auto.series.Enums.SerieStatus;
import com.auto.series.Exception.SerieNotFoundException;
import com.auto.series.Mapper.QuestionMapper;
import com.auto.series.Mapper.SerieMapper;
import com.auto.series.Repository.QuestionRepository;
import com.auto.series.Repository.SerieHistoryRepository;
import com.auto.series.Repository.SerieRepository;
import com.auto.series.Service.SerieEventProducer;
import com.auto.series.Service.SerieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SerieServiceImpl implements SerieService {

    private final SerieRepository serieRepository;
    private final QuestionRepository questionRepository;
    private final SerieHistoryRepository serieHistoryRepository;
    private final SerieMapper serieMapper;
    private final QuestionMapper questionMapper;
    private final SerieEventProducer serieEventProducer;

    @Override
    @Transactional
    public SerieResponse create(SerieRequest request, String createdBy) {
        Serie serie = serieMapper.toEntity(request, createdBy);
        Serie saved = serieRepository.save(serie);

        serieEventProducer.publishSerieCreated(saved.getId(), saved.getTitle(), saved.getTheme(), saved.getCreatedBy());
        return serieMapper.toResponse(saved);
    }

    @Override
    public PageResponse<SerieResponse> findAll(SerieFilterRequest filter) {
        Page<Serie> page = serieRepository.findWithFilters(
                SerieStatus.ACTIVE,
                filter.getTheme(),
                filter.getPremium(),
                filter.getDifficulty(),
                PageRequest.of(filter.getPage(), filter.getSize())
        );
        return PageResponse.from(page, serieMapper::toResponse);
    }

    @Override
    public PageResponse<SerieResponse> findPremium(SerieFilterRequest filter) {
        Page<Serie> page = serieRepository.findByPremiumTrueAndStatus(
                SerieStatus.ACTIVE, PageRequest.of(filter.getPage(), filter.getSize()));
        return PageResponse.from(page, serieMapper::toResponse);
    }

    @Override
    public PageResponse<SerieResponse> findFree(SerieFilterRequest filter) {
        Page<Serie> page = serieRepository.findByPremiumFalseAndStatus(
                SerieStatus.ACTIVE, PageRequest.of(filter.getPage(), filter.getSize()));
        return PageResponse.from(page, serieMapper::toResponse);
    }

    @Override
    public PageResponse<SerieResponse> findByTheme(String theme, SerieFilterRequest filter) {
        Page<Serie> page = serieRepository.findByThemeAndStatus(
                theme, SerieStatus.ACTIVE, PageRequest.of(filter.getPage(), filter.getSize()));
        return PageResponse.from(page, serieMapper::toResponse);
    }

    @Override
    public SerieDetailResponse findById(String id) {
        Serie serie = getActiveOrThrow(id);
        return serieMapper.toDetailResponse(serie);
    }

    @Override
    @Transactional
    public SerieResponse update(String id, SerieRequest request, String modifiedBy) {
        Serie serie = getActiveOrThrow(id);
        recordHistoryIfChanged(serie, request, modifiedBy);
        serieMapper.updateEntity(serie, request);
        Serie saved = serieRepository.save(serie);

        serieEventProducer.publishSerieUpdated(saved.getId(), modifiedBy);
        return serieMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(String id, String modifiedBy) {
        Serie serie = getActiveOrThrow(id);
        serie.setStatus(SerieStatus.DELETED);
        serieRepository.save(serie);
        logHistory(id, modifiedBy, "status", SerieStatus.ACTIVE.name(), SerieStatus.DELETED.name());

        serieEventProducer.publishSerieDeleted(id, modifiedBy);
    }

    @Override
    @Transactional
    public void togglePremium(String id, String modifiedBy) {
        Serie serie = getActiveOrThrow(id);
        boolean previous = serie.isPremium();
        serie.setPremium(!previous);
        serieRepository.save(serie);
        logHistory(id, modifiedBy, "premium", String.valueOf(previous), String.valueOf(!previous));
    }

    @Override
    @Transactional
    public SerieResponse duplicate(String id, String createdBy) {
        Serie original = getActiveOrThrow(id);

        Serie copy = Serie.builder()
                .title(original.getTitle() + " (copie)")
                .theme(original.getTheme())
                .description(original.getDescription())
                .difficulty(original.getDifficulty())
                .premium(original.isPremium())
                .status(SerieStatus.ACTIVE)
                .createdBy(createdBy)
                .build();
        Serie savedCopy = serieRepository.save(copy);

        List<Question> copiedQuestions = new ArrayList<>();
        for (Question q : original.getQuestions()) {
            copiedQuestions.add(Question.builder()
                    .serieId(savedCopy.getId())
                    .text(q.getText())
                    .imageUrl(q.getImageUrl())
                    .questionType(q.getQuestionType())
                    .options(new ArrayList<>(q.getOptions()))
                    .correctAnswerIndices(new ArrayList<>(q.getCorrectAnswerIndices()))
                    .explanation(q.getExplanation())
                    .tutorialLink(q.getTutorialLink())
                    .orderIndex(q.getOrderIndex())
                    .build());
        }
        questionRepository.saveAll(copiedQuestions);

        return serieMapper.toResponse(savedCopy);
    }

    @Override
    @Transactional
    public void archive(String id, String modifiedBy) {
        Serie serie = getActiveOrThrow(id);
        serie.setStatus(SerieStatus.ARCHIVED);
        serieRepository.save(serie);
        logHistory(id, modifiedBy, "status", SerieStatus.ACTIVE.name(), SerieStatus.ARCHIVED.name());
    }

    // ===== Helpers =====

    private Serie getActiveOrThrow(String id) {
        return serieRepository.findByIdAndStatusNot(id, SerieStatus.DELETED)
                .orElseThrow(() -> new SerieNotFoundException("Série introuvable : " + id));
    }

    private void recordHistoryIfChanged(Serie serie, SerieRequest request, String modifiedBy) {
        if (!serie.getTitle().equals(request.getTitle())) {
            logHistory(serie.getId(), modifiedBy, "title", serie.getTitle(), request.getTitle());
        }
        if (!serie.getDifficulty().equals(request.getDifficulty())) {
            logHistory(serie.getId(), modifiedBy, "difficulty",
                    serie.getDifficulty().name(), request.getDifficulty().name());
        }
    }

    private void logHistory(String serieId, String modifiedBy, String field, String oldValue, String newValue) {
        SeriesHistory history = SeriesHistory.builder()
                .seriesId(serieId)
                .modifiedBy(modifiedBy)
                .fieldChanged(field)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        serieHistoryRepository.save(history);
    }
}
