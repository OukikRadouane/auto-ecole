package com.auto.series.Dto.Response;

import com.auto.series.Enums.ExamStatus;
import com.auto.series.Enums.ExamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ExamResponse {
    private String id;
    private ExamType examType;
    private ExamStatus status;
    private Integer durationSeconds;
    private int totalQuestions;
    private List<ExamQuestionResponse> questions;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
}
