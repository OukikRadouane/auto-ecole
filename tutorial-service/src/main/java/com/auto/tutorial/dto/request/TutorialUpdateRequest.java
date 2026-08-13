package com.auto.tutorial.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialUpdateRequest {

    @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
    private String title;

    private String description;

    private String categoryId;

    private String difficulty;

    private String content;

    private Integer displayOrder;

    private Integer estimatedDuration;

    private String status;

    private String accessType;

    @Valid
    private List<ContentCreateRequest> contents;
}