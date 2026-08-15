package com.auto.notification.controller;

import com.auto.notification.events.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications/test/kafka")
public class KafkaTestController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-events:user-registered}")
    private String userTopic;

    @Value("${kafka.topics.exam-events:exam-submitted}")
    private String examTopic;

    @Value("${kafka.topics.registration-rejected:registration-events}")
    private String registrationTopic;

    @Value("${kafka.topics.milestone-reached:progress-events}")
    private String progressTopic;

    public KafkaTestController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // ════════════════════════════════════════════════════════════════
    // 1. USER CREATED (Bienvenue)
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/publish-user-created")
    public ResponseEntity<String> publishUserCreated(@RequestBody UserCreatedEvent event) {
        kafkaTemplate.send(userTopic, event.getUserId(), event);
        return ResponseEntity.ok("✅ USER_CREATED envoyé sur : " + userTopic);
    }

    // ════════════════════════════════════════════════════════════════
    // 2. EXAM COMPLETED (Résultats)
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/publish-exam-completed")
    public ResponseEntity<String> publishExamCompleted(@RequestBody ExamCompletedEvent event) {
        kafkaTemplate.send(examTopic, event.getUserId(), event);
        return ResponseEntity.ok("✅ EXAM_COMPLETED envoyé sur : " + examTopic);
    }

    // ════════════════════════════════════════════════════════════════
    // 3. REGISTRATION PROCESSED (Confirmation)
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/publish-registration-processed")
    public ResponseEntity<String> publishRegistrationProcessed(@RequestBody RegistrationProcessedEvent event) {
        kafkaTemplate.send(registrationTopic, event.getUserId(), event);
        return ResponseEntity.ok("✅ REGISTRATION_PROCESSED envoyé sur : " + registrationTopic);
    }

    // ════════════════════════════════════════════════════════════════
    // 4. REGISTRATION REJECTED (Rejet)
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/publish-registration-rejected")
    public ResponseEntity<String> publishRegistrationRejected(@RequestBody RegistrationRejectedEvent event) {
        kafkaTemplate.send(registrationTopic, event.getUserId(), event);
        return ResponseEntity.ok("✅ REGISTRATION_REJECTED envoyé sur : " + registrationTopic);
    }

    // ════════════════════════════════════════════════════════════════
    // 5. MILESTONE REACHED (Palier)
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/publish-milestone")
    public ResponseEntity<String> publishMilestone(@RequestBody MilestoneEvent event) {
        kafkaTemplate.send(progressTopic, event.getUserId(), event);
        return ResponseEntity.ok("✅ MILESTONE_REACHED envoyé sur : " + progressTopic);
    }

    // ════════════════════════════════════════════════════════════════
    // 6. USER INACTIVE (Rappel)
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/publish-user-inactive")
    public ResponseEntity<String> publishUserInactive(@RequestBody InactiveUserEvent event) {
        kafkaTemplate.send(progressTopic, event.getUserId(), event);
        return ResponseEntity.ok("✅ USER_INACTIVE envoyé sur : " + progressTopic);
    }

    // ════════════════════════════════════════════════════════════════
    // 7. PUBLISHER GÉNÉRIQUE (pour tout autre événement)
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/publish-generic")
    public ResponseEntity<String> publishGeneric(
            @RequestParam String topic,
            @RequestBody Object event) {
        kafkaTemplate.send(topic, "generic-key", event);
        return ResponseEntity.ok("✅ Événement envoyé sur : " + topic);
    }
}