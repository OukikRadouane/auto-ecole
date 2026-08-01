package com.auto.series.Dto.Request;

import com.auto.series.Enums.Difficulty;
import lombok.Data;

@Data
public class SerieFilterRequest {
    private String theme;
    private Boolean premium;
    private Difficulty difficulty;

    // pagination — valeurs par défaut sûres si absentes de la query string
    private int page = 0;
    private int size = 5;
}
