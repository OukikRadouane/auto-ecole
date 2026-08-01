package com.auto.series.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrainingRequest {
    @NotBlank
    private String serieId;
}
