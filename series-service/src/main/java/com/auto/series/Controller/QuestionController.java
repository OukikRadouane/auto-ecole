package com.auto.series.Controller;

import com.auto.series.Dto.Request.QuestionRequest;
import com.auto.series.Dto.Request.ReorderQuestionsRequest;
import com.auto.series.Dto.Response.QuestionResponse;
import com.auto.series.Service.AudioService;
import com.auto.series.Service.ImageService;
import com.auto.series.Service.QuestionService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/series/{serieId}/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final ImageService imageService;
    private final AudioService audioService;

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> findBySerie(@PathVariable String serieId){
        return ResponseEntity.ok(questionService.findBySerie(serieId));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> findById(
            @PathVariable String serieId,
            @PathVariable String questionId
    ){
        return ResponseEntity.ok(questionService.findById(serieId, questionId));
    }

    @PostMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable String serieId,
            @Valid @RequestBody QuestionRequest request
            ){
        QuestionResponse response = questionService.addQuestion(serieId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponse> update(
            @PathVariable String serieId,
            @PathVariable String questionId,
            @Valid @RequestBody QuestionRequest request
    ){
        return ResponseEntity.ok(questionService.update(serieId,questionId,request));
    }

    @DeleteMapping("/{questionId}")
    public  ResponseEntity<Void> delete(
            @PathVariable String serieId,
            @PathVariable String questionId
    ){
        questionService.delete(serieId, questionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reorder(
            @PathVariable String serieId,
            @Valid @RequestBody ReorderQuestionsRequest request
            ){
        questionService.reorder(serieId,request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{questionId}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable String serieId,
            @PathVariable String questionId,
            @RequestParam("file")MultipartFile file
            ){
        String imageKey = imageService.uploadQuestionImage(file, questionId);
        return ResponseEntity.ok(Map.of("imageUrl", imageKey));
    }

    @PostMapping("/{questionId}/audio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> uploadAudio(
            @PathVariable String serieId,
            @PathVariable String questionId,
            @RequestParam("file") MultipartFile file
    ) {
        String audioKey = audioService.uploadQuestionAudio(file, questionId);
        return ResponseEntity.ok(Map.of("audioUrl", audioKey));
    }

    // Import/export CSV : voir note en fin de réponse — pas encore implémenté
}
