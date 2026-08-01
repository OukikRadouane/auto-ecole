package com.auto.series.Dto.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class RandomExamRequest {
    @NotEmpty
    private List<String> seriesIds; // séries dans lesquelles piocher

    @Positive
    private Integer questionCount = 40;
}
