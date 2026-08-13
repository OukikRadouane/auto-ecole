package com.auto.tutorial.mapper;

import com.auto.tutorial.dto.request.TutorialCreateRequest;
import com.auto.tutorial.dto.request.TutorialUpdateRequest;
import com.auto.tutorial.dto.response.TutorialDetailResponse;
import com.auto.tutorial.dto.response.TutorialResponse;
import com.auto.tutorial.entity.Tutorial;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class, ContentMapper.class})
public interface TutorialMapper {

    // ─── CREATE ───
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "contents", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "estimatedDuration", ignore = true)
    @Mapping(target = "accessType", ignore = true)
    Tutorial toEntity(TutorialCreateRequest request);

    // ─── UPDATE ───
    void updateEntity(@MappingTarget Tutorial tutorial, TutorialUpdateRequest request);

    // ─── RESPONSE ───
    @Mapping(source = "category", target = "category")
    @Mapping(source = "totalContents", target = "totalContents")
    @Mapping(source = "totalDuration", target = "totalDuration")
    @Mapping(target = "hasVideo", expression = "java(tutorial.hasVideo())")
    @Mapping(target = "hasPdf", expression = "java(tutorial.hasPdf())")
    TutorialResponse toResponse(Tutorial tutorial);

    // ─── DETAIL RESPONSE ───
    @Mapping(source = "category", target = "category")
    @Mapping(source = "contents", target = "contents")
    @Mapping(source = "totalContents", target = "totalContents")
    @Mapping(source = "totalDuration", target = "totalDuration")
    @Mapping(target = "hasVideo", expression = "java(tutorial.hasVideo())")
    @Mapping(target = "hasPdf", expression = "java(tutorial.hasPdf())")
    TutorialDetailResponse toDetailResponse(Tutorial tutorial);

    // ─── HELPERS ───
    default String mapDifficulty(Tutorial.Difficulty difficulty) {
        return difficulty != null ? difficulty.name() : null;
    }

    default String mapStatus(Tutorial.Status status) {
        return status != null ? status.name() : null;
    }

    default String mapAccessType(Tutorial.AccessType accessType) {
        return accessType != null ? accessType.name() : null;
    }

    default Tutorial.Difficulty mapDifficultyString(String difficulty) {
        return difficulty != null ? Tutorial.Difficulty.valueOf(difficulty) : null;
    }

    default Tutorial.Status mapStatusString(String status) {
        return status != null ? Tutorial.Status.valueOf(status) : null;
    }

    default Tutorial.AccessType mapAccessTypeString(String accessType) {
        return accessType != null ? Tutorial.AccessType.valueOf(accessType) : null;
    }
}