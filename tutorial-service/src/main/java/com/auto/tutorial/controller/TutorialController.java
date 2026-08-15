package com.auto.tutorial.controller;

import com.auto.tutorial.dto.response.CategoryResponse;
import com.auto.tutorial.dto.response.TutorialDetailResponse;
import com.auto.tutorial.dto.response.TutorialResponse;
import com.auto.tutorial.service.CategoryService;
import com.auto.tutorial.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutorials")
@RequiredArgsConstructor
@Tag(name = "Tutorial API", description = "API publique pour les tutoriels")
@SecurityRequirement(name = "Bearer Authentication")
public class TutorialController {

    private final TutorialService tutorialService;
    private final CategoryService categoryService;

    @Operation(summary = "Récupérer tous les tutoriels publiés")
    @GetMapping
    public ResponseEntity<Page<TutorialResponse>> getTutorials(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @ParameterObject
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(tutorialService.getPublishedTutorials(pageable, search, category, difficulty));
    }

    @Operation(summary = "Récupérer le détail d'un tutoriel")
    @GetMapping("/{id}")
    public ResponseEntity<TutorialDetailResponse> getTutorialDetail(@PathVariable String id) {
        return ResponseEntity.ok(tutorialService.getTutorialDetail(id));
    }

    @Operation(summary = "Récupérer les tutoriels d'une catégorie")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TutorialResponse>> getTutorialsByCategory(@PathVariable String categoryId) {
        return ResponseEntity.ok(tutorialService.getTutorialsByCategory(categoryId));
    }

    @Operation(summary = "Récupérer toutes les catégories")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Récupérer les catégories avec tutoriels publiés")
    @GetMapping("/categories/with-tutorials")
    public ResponseEntity<List<CategoryResponse>> getCategoriesWithTutorials() {
        return ResponseEntity.ok(categoryService.getCategoriesWithPublishedTutorials());
    }

    @Operation(summary = "Récupérer tous les tutoriels publiés (liste simple)")
    @GetMapping("/all")
    public ResponseEntity<List<TutorialResponse>> getAllPublishedTutorials() {
        return ResponseEntity.ok(tutorialService.getPublishedTutorialsList());
    }

    @Operation(summary = "Récupérer les niveaux de difficulté")
    @GetMapping("/difficulties")
    public ResponseEntity<List<String>> getDifficulties() {
        return ResponseEntity.ok(List.of("BEGINNER", "INTERMEDIATE", "ADVANCED"));
    }
}