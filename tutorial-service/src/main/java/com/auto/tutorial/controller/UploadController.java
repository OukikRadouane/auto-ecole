package com.auto.tutorial.controller;

import com.auto.tutorial.dto.response.ContentResponse;
import com.auto.tutorial.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/tutorials")
@RequiredArgsConstructor
@Tag(name = "Admin Upload API", description = "Upload de vidéos et PDFs")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class UploadController {

    private final UploadService uploadService;

    // ─── UPLOAD VIDÉO ───
    @Operation(summary = "Uploader une vidéo (avec FFmpeg)")
    @PostMapping(value = "/{tutorialId}/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentResponse> uploadVideo(
            @PathVariable String tutorialId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {

        log.info("Requête d'upload vidéo pour le tutoriel : {}", tutorialId);

        ContentResponse response = uploadService.uploadVideo(tutorialId, file, title, description);
        return ResponseEntity.ok(response);
    }

    // ─── UPLOAD PDF ───
    @Operation(summary = "Uploader un PDF (nombre de pages détecté automatiquement)")
    @PostMapping(value = "/{tutorialId}/upload-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentResponse> uploadPdf(
            @PathVariable String tutorialId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description) {

        log.info("Requête d'upload PDF pour le tutoriel : {}", tutorialId);

        ContentResponse response = uploadService.uploadPdf(tutorialId, file, title, description);
        return ResponseEntity.ok(response);
    }
}