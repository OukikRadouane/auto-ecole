package com.auto.registration.service;

import com.auto.registration.entity.Registration;
import com.auto.registration.events.RegistrationProcessedEvent;
import com.auto.registration.events.RegistrationRejectedEvent;
import com.auto.registration.events.RegistrationSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.registration-events:registration-events}")
    private String registrationEventsTopic;

    public void publishRegistrationSubmitted(Registration registration) {
        RegistrationSubmittedEvent event = RegistrationSubmittedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("registration-submitted")
                .registrationId(registration.getId())
                .userId(registration.getUserId())
                .userEmail(registration.getUserEmail())
                .userFirstName(registration.getFirstName())
                .userLastName(registration.getLastName())
                .status(registration.getStatus().name())
                .timestamp(LocalDateTime.now())
                .source("registration-service")
                .build();

        publish(event);
        log.info("📤 registration-submitted pour {}", registration.getId());
    }

    public void publishRegistrationProcessed(Registration registration) {
        RegistrationProcessedEvent event = RegistrationProcessedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("registration-processed")
                .registrationId(registration.getId())
                .userId(registration.getUserId())
                .userEmail(registration.getUserEmail())
                .userFirstName(registration.getFirstName())
                .userLastName(registration.getLastName())
                .status(registration.getStatus().name())
                .message("Votre inscription a été validée")
                .timestamp(LocalDateTime.now())
                .source("registration-service")
                .build();

        publish(event);
        log.info("📤 registration-processed pour {}", registration.getId());
    }

    public void publishRegistrationRejected(Registration registration) {
        RegistrationRejectedEvent event = RegistrationRejectedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("registration-rejected")
                .registrationId(registration.getId())
                .userId(registration.getUserId())
                .userEmail(registration.getUserEmail())
                .userFirstName(registration.getFirstName())
                .userLastName(registration.getLastName())
                .reason(registration.getRejectedReason())
                .timestamp(LocalDateTime.now())
                .source("registration-service")
                .build();

        publish(event);
        log.info("📤 registration-rejected pour {}", registration.getId());
    }

    private void publish(Object event) {
        try {
            kafkaTemplate.send(registrationEventsTopic, event);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la publication de l'événement: {}", e.getMessage());
        }
    }
}