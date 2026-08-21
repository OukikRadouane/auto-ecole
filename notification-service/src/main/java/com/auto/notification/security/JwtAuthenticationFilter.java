package com.auto.notification.security;

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
import java.util.Collections;
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

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                String userId = jwtService.getUserId(token);
                String email = jwtService.getEmail(token);
                String role = jwtService.getRole(token);

                // 1. Formater correctement le rôle pour Spring Security
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();
                if (role != null && !role.isBlank()) {
                    String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    authorities = List.of(new SimpleGrantedAuthority(formattedRole));
                }

                UserPrincipal userPrincipal = UserPrincipal.builder()
                        .id(userId)
                        .email(email)
                        .role(role)
                        .build();

                // 2. Transmettre les autorisations au token d'authentification
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("User authenticated: {} with authority: {}", email, authorities);
            }

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}