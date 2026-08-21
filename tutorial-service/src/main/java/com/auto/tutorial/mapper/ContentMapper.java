package com.auto.tutorial.mapper;

import com.auto.tutorial.dto.request.ContentCreateRequest;
import com.auto.tutorial.dto.response.ContentResponse;
import com.auto.tutorial.entity.Content;
import com.auto.tutorial.entity.Content.ContentType;
import com.auto.tutorial.service.StorageService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ContentMapper {

    @Autowired
    protected StorageService storageService; // 👈 Injection par Spring dans MapStruct

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
    public abstract Content toEntity(ContentCreateRequest request);

    // ─── RESPONSE ───
    @Mapping(target = "fileKey", source = "fileKey") // 👈 Garantit le mapping du fileKey
    @Mapping(target = "storageUrl", ignore = true)   // Ignoré ici, géré par @AfterMapping
    @Mapping(target = "thumbnail", ignore = true)    // Ignoré ici, géré par @AfterMapping
    @Mapping(target = "formattedDuration", expression = "java(content.hasDuration() ? content.getFormattedDuration() : null)")
    @Mapping(target = "durationDisplay", expression = "java(content.getDurationDisplay())")
    @Mapping(target = "pageCountDisplay", expression = "java(content.getPageCountDisplay())")
    @Mapping(target = "isVideo", expression = "java(content.isVideo())")
    @Mapping(target = "isPdf", expression = "java(content.isPdf())")
    public abstract ContentResponse toResponse(Content content);

    // ─── HOOK APRES MAPPING (Génération URLs fraîches) ───
    @AfterMapping
    protected void enrichUrls(Content content, @MappingTarget ContentResponse response) {
        if (storageService == null) return;

        // 1. URL du fichier principal (vidéo/PDF)
        if (content.getFileKey() != null && !content.getFileKey().isBlank()) {
            response.setStorageUrl(storageService.getFileUrl(content.getFileKey()));
        }

        // 2. URL de la miniature (Thumbnail)
        String thumbnail = content.getThumbnail();
        if (thumbnail != null && !thumbnail.isBlank()) {

            // Si une ancienne URL complète (et expirée) a été stockée en BDD (ex: http://localhost:9000/tutorials/thumbnails/...)
            if (thumbnail.startsWith("http")) {
                int bucketIndex = thumbnail.indexOf("/tutorials/");
                if (bucketIndex != -1) {
                    // On isole la clef (ex: thumbnails/xyz.jpg) en retirant le domaine
                    String keyWithParams = thumbnail.substring(bucketIndex + "/tutorials/".length());

                    // On retire les paramètres MinIO expirés (?X-Amz-...)
                    String cleanKey = keyWithParams.contains("?")
                            ? keyWithParams.substring(0, keyWithParams.indexOf("?"))
                            : keyWithParams;

                    // Enfin, on génère une URL toute neuve
                    response.setThumbnail(storageService.getFileUrl(cleanKey));
                } else {
                    // Si c'est un lien externe (ex: imgur, aws s3 public), on le laisse tel quel
                    response.setThumbnail(thumbnail);
                }
            } else {
                // Comportement normal : c'est déjà une clef relative (ex: "thumbnails/123.jpg")
                response.setThumbnail(storageService.getFileUrl(thumbnail));
            }
        }
    }

    // ─── HELPERS ───
    public ContentType mapContentType(String contentType) {
        return contentType != null ? ContentType.valueOf(contentType.toUpperCase()) : null;
    }

    public String mapContentTypeToString(ContentType contentType) {
        return contentType != null ? contentType.name() : null;
    }
}