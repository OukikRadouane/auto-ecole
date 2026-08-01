package com.auto.series.Dto.Request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExamStartRequest {
    @NotBlank
    private String seriesId;
}
