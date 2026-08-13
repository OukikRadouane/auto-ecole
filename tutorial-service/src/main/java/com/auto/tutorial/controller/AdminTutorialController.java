package com.auto.tutorial.controller;

import com.auto.tutorial.dto.request.TutorialCreateRequest;
import com.auto.tutorial.dto.request.TutorialUpdateRequest;
import com.auto.tutorial.dto.response.TutorialResponse;
import com.auto.tutorial.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tutorials")
@RequiredArgsConstructor
@Tag(name = "Admin Tutorial API", description = "API d'administration")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTutorialController {

    private final TutorialService tutorialService;

    @Operation(summary = "Créer un tutoriel")
    @PostMapping
    public ResponseEntity<TutorialResponse> createTutorial(@Valid @RequestBody TutorialCreateRequest request) {
        return ResponseEntity.ok(tutorialService.createTutorial(request));
    }

    @Operation(summary = "Modifier un tutoriel")
    @PutMapping("/{id}")
    public ResponseEntity<TutorialResponse> updateTutorial(
            @PathVariable String id,
            @Valid @RequestBody TutorialUpdateRequest request) {
        return ResponseEntity.ok(tutorialService.updateTutorial(id, request));
    }

    @Operation(summary = "Supprimer un tutoriel")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTutorial(@PathVariable String id) {
        tutorialService.deleteTutorial(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Publier un tutoriel")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<TutorialResponse> publishTutorial(@PathVariable String id) {
        return ResponseEntity.ok(tutorialService.publishTutorial(id));
    }

    @Operation(summary = "Archiver un tutoriel")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<TutorialResponse> archiveTutorial(@PathVariable String id) {
        return ResponseEntity.ok(tutorialService.archiveTutorial(id));
    }

    @Operation(summary = "Récupérer tous les tutoriels (admin)")
    @GetMapping
    public ResponseEntity<Page<TutorialResponse>> getAllTutorials(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(tutorialService.getAllTutorials(pageable, search, status));
    }
}