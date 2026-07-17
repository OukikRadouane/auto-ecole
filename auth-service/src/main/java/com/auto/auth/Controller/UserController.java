package com.auto.auth.Controller;

import com.auto.auth.Dto.Request.UpdateProfileRequest;
import com.auto.auth.Dto.Request.UpdateRoleRequest;
import com.auto.auth.Dto.Response.UserResponse;
import com.auto.auth.Service.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

// endpoints needs authentification
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication){
        String userId = getUserIdFromAuthentication(authentication);
        UserResponse response = userService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ){
        String userId = getUserIdFromAuthentication(authentication);
        UserResponse response = userService.updateMyProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        userService.deleteMyProfile(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/export")
    public ResponseEntity<String> exportMyData(Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        String data = userService.exportMyData(userId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Content-Disposition", "attachment; filename=user-data.json")
                .body(data);
    }
    // ===== Endpoints Admin (ROLE_ADMIN requis) =====
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable String id,
            @Valid @RequestBody UpdateRoleRequest request,
            Authentication authentication
    ) {
        // Empêcher un admin de modifier son propre rôle
        String adminId = getUserIdFromAuthentication(authentication);
        if (adminId.equals(id)) {
            throw new ForbiddenException("Vous ne pouvez pas modifier votre propre rôle");
        }

        UserResponse response = userService.updateUserRole(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            Authentication authentication
    ){
        // Empêcher un admin de se supprimer lui-même
        String adminId = getUserIdFromAuthentication(authentication);
        if (adminId.equals(id)) {
            throw new ForbiddenException("Vous ne pouvez pas vous supprimer vous-même");
        }

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Méthodes utilitaires =====
    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Utilisateur non authentifié");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Retourne l'ID de l'utilisateur
        }

        return principal.toString();
    }

}
