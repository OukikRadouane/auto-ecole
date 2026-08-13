package com.auto.tutorial.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {

    private String id;
    private String title;
    private String description;
    private String contentType;
    private String storageUrl;
    private String fileType;
    private Integer displayOrder;
    private Boolean isRequired;

    // ─── SPÉCIFIQUE VIDÉO ───
    private Integer duration;
    private String formattedDuration;
    private String durationDisplay;
    private String thumbnail;
    private String transcodingStatus;

    // ─── SPÉCIFIQUE PDF ───
    private Integer pageCount;
    private String pageCountDisplay;

    // ─── UTILES ───
    private Boolean isVideo;
    private Boolean isPdf;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}