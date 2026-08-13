package com.auto.tutorial.mapper;

import com.auto.tutorial.dto.request.ContentCreateRequest;
import com.auto.tutorial.dto.response.ContentResponse;
import com.auto.tutorial.entity.Content;
import com.auto.tutorial.entity.Content.ContentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContentMapper {

    // ─── CREATE ───
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutorial", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "transcodingStatus", ignore = true)
    @Mapping(target = "storageUrl", ignore = true)
    @Mapping(target = "fileKey", ignore = true)
    @Mapping(target = "fileType", ignore = true)
    @Mapping(target = "thumbnail", ignore = true)
    @Mapping(target = "duration", ignore = true)
    @Mapping(target = "pageCount", ignore = true)
    Content toEntity(ContentCreateRequest request);

    // ─── RESPONSE ───
    @Mapping(target = "formattedDuration", expression = "java(content.hasDuration() ? content.getFormattedDuration() : null)")
    @Mapping(target = "durationDisplay", expression = "java(content.getDurationDisplay())")
    @Mapping(target = "pageCountDisplay", expression = "java(content.getPageCountDisplay())")
    @Mapping(target = "isVideo", expression = "java(content.isVideo())")
    @Mapping(target = "isPdf", expression = "java(content.isPdf())")
    ContentResponse toResponse(Content content);

    // ─── HELPERS ───
    default ContentType mapContentType(String contentType) {
        return contentType != null ? ContentType.valueOf(contentType.toUpperCase()) : null;
    }

    default String mapContentTypeToString(ContentType contentType) {
        return contentType != null ? contentType.name() : null;
    }
}