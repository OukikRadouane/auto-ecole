package com.auto.series.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SerieStatisticsResponse {
    private String seriesId;
    private long totalAttempts;
    private double averageScore;
    private double passRate; // pourcentage de tentatives réussies
    private long questionCount;
}
