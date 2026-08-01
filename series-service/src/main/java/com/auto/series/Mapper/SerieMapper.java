package com.auto.series.Mapper;

import com.auto.series.Dto.Request.SerieRequest;
import com.auto.series.Dto.Response.SerieDetailResponse;
import com.auto.series.Dto.Response.SerieResponse;
import com.auto.series.Entity.Serie;
import com.auto.series.Enums.SerieStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SerieMapper {

    private final QuestionMapper questionMapper;

    public SerieResponse toResponse(Serie serie){
        return SerieResponse.builder()
                .id(serie.getId())
                .title(serie.getTitle())
                .theme(serie.getTheme())
                .description(serie.getDescription())
                .difficulty(serie.getDifficulty())
                .premium(serie.isPremium())
                .questionCount(serie.getQuestionCount())
                .createdAt(serie.getCreatedAt())
                .updatedAt(serie.getUpdatedAt())
                .build();
    }

    public SerieDetailResponse toDetailResponse(Serie serie) {
        return SerieDetailResponse.builder()
                .id(serie.getId())
                .title(serie.getTitle())
                .theme(serie.getTheme())
                .description(serie.getDescription())
                .difficulty(serie.getDifficulty())
                .premium(serie.isPremium())
                .questions(serie.getQuestions().stream()
                        .map(questionMapper::toResponse)
                        .toList())
                .createdAt(serie.getCreatedAt())
                .updatedAt(serie.getUpdatedAt())
                .build();
    }

    public Serie toEntity(SerieRequest request, String createdBy) {
        return Serie.builder()
                .title(request.getTitle())
                .theme(request.getTheme())
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .premium(request.isPremium())
                .status(SerieStatus.ACTIVE)
                .createdBy(createdBy)
                .build();
    }

    public void updateEntity(Serie serie, SerieRequest request) {
        serie.setTitle(request.getTitle());
        serie.setTheme(request.getTheme());
        serie.setDescription(request.getDescription());
        serie.setDifficulty(request.getDifficulty());
        serie.setPremium(request.isPremium());
    }
}
