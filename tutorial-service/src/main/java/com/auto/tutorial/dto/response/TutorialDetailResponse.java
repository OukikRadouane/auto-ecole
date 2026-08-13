package com.auto.tutorial.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialDetailResponse {

    private String id;
    private String title;
    private String description;
    private CategoryResponse category;
    private String difficulty;
    private String content;
    private Integer displayOrder;
    private Integer estimatedDuration;
    private String status;
    private String accessType;
    private Integer viewCount;
    private List<ContentResponse> contents;
    private Integer totalContents;
    private Integer totalDuration;
    private Boolean hasVideo;
    private Boolean hasPdf;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}