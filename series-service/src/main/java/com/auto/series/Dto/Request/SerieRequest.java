package com.auto.series.Dto.Request;

import com.auto.series.Enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SerieRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    @Size(max = 100)
    private String theme;

    @Size(max = 2000)
    private String description;

    @NotNull
    private Difficulty difficulty;

    private boolean premium = false;
    private BigDecimal price;
}
