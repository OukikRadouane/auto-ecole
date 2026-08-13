package com.auto.registration.service;

import com.auto.registration.events.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumerService {

    // ✅ Seulement pour LOGGER - PAS de création automatique
    @KafkaListener(topics = "user-events", groupId = "registration-service-group")
    public void consumeUserCreated(UserCreatedEvent event) {
        log.info("📥 Nouvel utilisateur créé: userId={}, email={}",
                event.getUserId(), event.getUserEmail());
        log.info("ℹ️ L'utilisateur devra soumettre une demande d'inscription séparément.");
        // ❌ NE PAS créer de demande automatiquement
    }
}