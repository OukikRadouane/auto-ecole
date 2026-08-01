package com.auto.series.Dto.Response;

import com.auto.series.Enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SerieResponse {
    private String id;
    private String title;
    private String theme;
    private String description;
    private Difficulty difficulty;
    private boolean premium;
    private int questionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
