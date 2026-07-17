package com.auto.auth.Security;

import com.auto.auth.Entity.RefreshToken;
import com.auto.auth.Entity.User;
import com.auto.auth.Repository.RefreshTokenRepo;
import com.auto.auth.Repository.UserRepo;
import com.auto.auth.Service.UserEventProducer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserEventProducer eventProducer;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException{
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email non trouvé");
            return;
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé");
            return;
        }

        // Générer les tokens JWT
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshTokenValue();

        // Sauvegarder le refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(refreshTokenValue)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        refreshTokenRepo.save(refreshToken);

        // Rediriger vers le frontend avec les tokens
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshTokenValue)
                .queryParam("expiresIn", jwtService.getAccessTokenExpirySeconds())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        // Publier l'événement si c'est un nouvel utilisateur
        if (user.getCreatedAt() != null && user.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
            eventProducer.publishUserRegistered(user.getId(), user.getEmail());
        }

        log.info("Utilisateur OAuth connecté: {}", email);
    }
}
