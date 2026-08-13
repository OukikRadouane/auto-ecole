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
public class TutorialResponse {

    private String id;
    private String title;
    private String description;
    private CategoryResponse category;
    private String difficulty;
    private Integer displayOrder;
    private Integer estimatedDuration;
    private String status;
    private String accessType;
    private Integer viewCount;
    private Integer totalContents;
    private Integer totalDuration;
    private Boolean hasVideo;
    private Boolean hasPdf;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}