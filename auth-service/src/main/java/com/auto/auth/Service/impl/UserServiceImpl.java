package com.auto.auth.Service.impl;

import com.auto.auth.Dto.Request.UpdateProfileRequest;
import com.auto.auth.Dto.Request.UpdateRoleRequest;
import com.auto.auth.Dto.Response.UserResponse;
import com.auto.auth.Entity.User;
import com.auto.auth.Exception.UserNotFoundException;
import com.auto.auth.Repository.RefreshTokenRepo;
import com.auto.auth.Repository.UserRepo;
import com.auto.auth.Service.UserEventProducer;
import com.auto.auth.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserEventProducer eventProducer;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getMyProfile(String userId) {
        User user = findUserById(userId);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(String userId, UpdateProfileRequest request) {
        User user = findUserById(userId);

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User updatedUser = userRepo.save(user);
        log.info("Profil utilisateur mis à jour: {}", userId);

        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteMyProfile(String userId) {
        User user = findUserById(userId);

        // Révoquer tous les refresh tokens
        refreshTokenRepo.revokeAllByUserId(userId);

        // Supprimer l'utilisateur
        userRepo.delete(user);
        log.info("Utilisateur supprimé: {}", userId);

        eventProducer.publishUserDeleted(userId);

    }

    @Override
    public String exportMyData(String userId) {
        User user = findUserById(userId);
        return String.format("""
                        {
                                    "id": "%s",
                                    "email": "%s",
                                    "firstName": "%s",
                                    "lastName": "%s",
                                    "phone": "%s",
                                    "role": "%s",
                                    "emailVerified": %b,
                                    "createdAt": "%s",
                                    "updatedAt": "%s",
                                    "oauthProvider": "%s"
                        }
                        """,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole().name(),
                user.isEmailVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getOauthProvider() != null ? user.getOauthProvider() : "null"
        );
    }


    // === Méthodes Admin ===

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = findUserById(userId);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(String userId, UpdateRoleRequest request) {
        User user = findUserById(userId);

        user.setRole(request.getRole());
        User updatedUser = userRepo.save(user);
        log.info("Rôle de l'utilisateur {} mis à jour: {}", userId, request.getRole());

        return mapToUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {

        User user = findUserById(userId);

        // Révoquer tous les refresh tokens
        refreshTokenRepo.revokeAllByUserId(userId);

        // Supprimer l'utilisateur
        userRepo.delete(user);
        log.info("Utilisateur {} supprimé par admin", userId);
        eventProducer.publishUserDeleted(userId);
    }

    private User findUserById(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + userId));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .build();
    }
}
