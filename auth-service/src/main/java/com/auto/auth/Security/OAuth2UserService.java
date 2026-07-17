package com.auto.auth.Security;

import com.auto.auth.Entity.User;
import com.auto.auth.Enums.Role;
import com.auto.auth.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepo userRepo;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String oauthId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email non fourni par le fournisseur OAuth");
        }

        Optional<User> existingUser = userRepo.findByEmail(email);
        User user;

        if (existingUser.isPresent()){
            user = existingUser.get();
// Mettre à jour les informations si nécessaire
            if (user.getOauthProvider() == null) {
                user.setOauthProvider(provider);
                user.setOauthId(oauthId);
                user.setEmailVerified(true); // Les emails OAuth sont déjà vérifiés
            }
        } else {
            user = User.builder()
                    .email(email)
                    .firstName(firstName != null ? firstName : "Utilisateur")
                    .lastName(lastName != null ? lastName : "OAuth")
                    .passwordHash("")
                    .role(Role.STUDENT)
                    .emailVerified(true)
                    .enabled(true)
                    .oauthProvider(provider)
                    .oauthId(oauthId)
                    .build();

            userRepo.save(user);
            log.info("Nouvel utilisateur OAuth créé: {}", email);
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "sub"
        );
    }
}
