package com.auto.series.Controller;

import com.auto.series.Dto.Request.ExamStartRequest;
import com.auto.series.Dto.Request.ExamSubmitRequest;
import com.auto.series.Dto.Request.RandomExamRequest;
import com.auto.series.Dto.Request.TrainingRequest;
import com.auto.series.Dto.Response.CorrectionResponse;
import com.auto.series.Dto.Response.ExamResponse;
import com.auto.series.Dto.Response.ExamResultResponse;
import com.auto.series.Service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/start")
    public ResponseEntity<ExamResponse> startExam(
            @Valid @RequestBody ExamStartRequest request,
            Authentication authentication
            ){
        ExamResponse response = examService.startExam(request.getSerieId(), authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/training")
    public ResponseEntity<ExamResponse> startTraining(
            @Valid @RequestBody TrainingRequest request,
            Authentication authentication
    ) {
        ExamResponse response = examService.startTraining(request.getSerieId(), authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/random")
    public ResponseEntity<ExamResponse> startRandomExam(
            @Valid @RequestBody RandomExamRequest request,
            Authentication authentication
    ) {
        ExamResponse response = examService.startRandomExam(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponse> getExam(
            @PathVariable String examId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(examService.getExam(examId, authentication.getName()));
    }

    @GetMapping("/resume")
    public ResponseEntity<ExamResponse> resumeExam(Authentication authentication) {
        return ResponseEntity.ok(examService.resumeExam(authentication.getName()));
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<ExamResultResponse> submitExam(
            @PathVariable String examId,
            @Valid @RequestBody ExamSubmitRequest request,
            Authentication authentication
    ) {
        ExamResultResponse response = examService.submitExam(examId, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{examId}/correction")
    public ResponseEntity<CorrectionResponse> getCorrection(
            @PathVariable String examId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(examService.getCorrection(examId, authentication.getName()));
    }
}
