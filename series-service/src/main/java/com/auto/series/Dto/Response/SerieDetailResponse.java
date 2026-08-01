package com.auto.series.Dto.Response;

import com.auto.series.Enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SerieDetailResponse {
    private String id;
    private String title;
    private String theme;
    private String description;
    private Difficulty difficulty;
    private boolean premium;
    private List<QuestionResponse> questions; // avec réponses correctes incluses (vue admin/détail)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
