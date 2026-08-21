package com.auto.tutorial.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        //  Pas de token -> on passe (les endpoints publics seront gérés par SecurityConfig)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                //  Extraction des informations
                String userId = jwtService.getUserId(token);
                String email = jwtService.getEmail(token);
                String role = jwtService.getRole(token);

                log.debug("🔐 Authentification réussie: userId={}, email={}, role={}", userId, email, role);

                // 👤 Création du UserPrincipal
                UserPrincipal userPrincipal = UserPrincipal.builder()
                        .id(userId)
                        .email(email)
                        .role(role)
                        .build();

                // ️ Création des autorités
                String formattedRole = role != null && !role.startsWith("ROLE_")
                        ? "ROLE_" + role
                        : role;

                List<SimpleGrantedAuthority> authorities = role != null && !role.isBlank()
                        ? List.of(new SimpleGrantedAuthority(formattedRole))
                        : List.of();

                //  Mise en contexte Spring Security
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        token,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("✅ User '{}' authentifié avec succès", email);
            }

        } catch (Exception e) {
            log.error("❌ Erreur d'authentification: {}", e.getMessage());
            // On ne bloque pas le flux, laisse SecurityConfig gérer l'accès
        }

        filterChain.doFilter(request, response);
    }

    //  Ne pas filtrer les endpoints publics (optimisation)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")
                || path.equals("/tutorials")
                || path.startsWith("/tutorials/categories")
                || path.equals("/tutorials/difficulties");
    }
}