package com.auto.tutorial.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentUpdateRequest {

    private String title;
    private String description;

    // SPÉCIFIQUE VIDÉO
    private Integer duration;
    private String thumbnail;

    // SPÉCIFIQUE PDF
    private Integer pageCount;

    private Integer displayOrder;
    private Boolean isRequired;
}