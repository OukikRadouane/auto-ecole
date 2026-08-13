package com.auto.tutorial.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TutorialCreateRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
    private String title;

    private String description;

    @NotBlank(message = "La catégorie est obligatoire")
    private String categoryId;

    @NotBlank(message = "La difficulté est obligatoire")
    private String difficulty;

    private String content;

    @NotNull(message = "L'ordre d'affichage est obligatoire")
    private Integer displayOrder;

    private Integer estimatedDuration;

    @Builder.Default
    private String accessType = "FREE";

    @Valid
    private List<ContentCreateRequest> contents;
}