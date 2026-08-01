package com.auto.series.Controller;

import com.auto.series.Dto.Request.SerieFilterRequest;
import com.auto.series.Dto.Request.SerieRequest;
import com.auto.series.Dto.Response.PageResponse;
import com.auto.series.Dto.Response.SerieDetailResponse;
import com.auto.series.Dto.Response.SerieResponse;
import com.auto.series.Service.SerieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SerieController {
    private final SerieService serieService;

    @GetMapping
    public ResponseEntity<PageResponse<SerieResponse>> findAll(@ModelAttribute SerieFilterRequest filter){
        return ResponseEntity.ok(serieService.findAll(filter));
    }

    @GetMapping("/premium")
    public ResponseEntity<PageResponse<SerieResponse>> findPremium(@ModelAttribute SerieFilterRequest filter){
        return ResponseEntity.ok(serieService.findPremium(filter));
    }

    @GetMapping("/free")
    public ResponseEntity<PageResponse<SerieResponse>> findFree(@ModelAttribute SerieFilterRequest filter) {
        return ResponseEntity.ok(serieService.findFree(filter));
    }

    @GetMapping("/theme/{theme}")
    public ResponseEntity<PageResponse<SerieResponse>> findByTheme(
            @PathVariable String theme,
            @ModelAttribute SerieFilterRequest filter
    ) {
        return ResponseEntity.ok(serieService.findByTheme(theme, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SerieDetailResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(serieService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SerieResponse> create(
            @Valid @RequestBody SerieRequest request,
            Authentication authentication
    ){
        SerieResponse response = serieService.create(request,authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SerieResponse> update(
            @PathVariable String id,
            @Valid @RequestBody SerieRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(serieService.update(id, request, authentication.getName()));
    }

    @PutMapping("/{id}/premium")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> togglePremium(@PathVariable String id, Authentication authentication) {
        serieService.togglePremium(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication authentication) {
        serieService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SerieResponse> duplicate(@PathVariable String id, Authentication authentication) {
        SerieResponse response = serieService.duplicate(id, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> archive(@PathVariable String id, Authentication authentication) {
        serieService.archive(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    // Statistiques, import/export : voir note en fin de réponse — pas encore implémentés
}
