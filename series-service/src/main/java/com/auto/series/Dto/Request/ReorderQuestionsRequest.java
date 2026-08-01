package com.auto.series.Dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ReorderQuestionsRequest {
    @NotEmpty
    private List<String> questionIdsInOrder;
}
