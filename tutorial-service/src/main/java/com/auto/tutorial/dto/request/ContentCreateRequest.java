package com.auto.tutorial.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentCreateRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    private String description;

    @NotNull(message = "Le type est obligatoire")
    private String contentType;  // VIDEO, PDF

    private String url;

    // ─── SPÉCIFIQUE VIDÉO ───
    private Integer duration;
    private String thumbnail;

    // ─── SPÉCIFIQUE PDF ───
    private Integer pageCount;

    private Integer displayOrder;
    private Boolean isRequired;
}