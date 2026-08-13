package com.auto.notification.service;

import com.auto.notification.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.handler.annotation.Payload;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventConsumerService {

    private final EmailService emailService;
    private final NotificationLogService logService;

    // ─── 1. EXAM COMPLETED ───
    @KafkaListener(topics = "${kafka.topics.exam-events:exam-submitted}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeExamCompleted(ExamCompletedEvent event) {
        log.info("📥 Reçu exam-completed: userId={}, score={}", event.getUserId(), event.getScore());

        String fullName = event.getUserFirstName() + " " + event.getUserLastName();
        String status = event.getPassed() ? "✅ Réussi" : "❌ Échoué";

        String subject = "📝 Résultat de votre examen blanc";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<p>Vous avez terminé votre examen blanc.</p>"
                + "<div style='background: #f0f4f8; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p><strong>📊 Score :</strong> " + event.getScore() + "/40</p>"
                + "  <p><strong>📈 Statut :</strong> " + status + "</p>"
                + "  <p><strong>✅ Bonnes réponses :</strong> " + event.getCorrectCount() + "</p>"
                + "  <p><strong>📚 Thème :</strong> " + (event.getSeriesTheme() != null ? event.getSeriesTheme() : "Général") + "</p>"
                + "</div>"
                + "<p>Continuez vos efforts !</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "exam-results", subject, body, event.getUserEmail());
            log.info("✅ Email de résultats envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "exam-results", subject, body, event.getUserEmail(), e.getMessage());
            log.error("❌ Échec d'envoi à {}", event.getUserEmail());
        }
    }

    // ─── 2. USER CREATED (Bienvenue) ───
    @KafkaListener(topics = "${kafka.topics.user-events:user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserCreated(UserCreatedEvent event) {
        log.info("📥 Reçu user-created: userId={}", event.getUserId());

        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        String subject = "🎉 Bienvenue sur Auto-École Pro !";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<p>Bienvenue sur Auto-École Pro ! 🚗</p>"
                + "<p>Votre compte a été créé avec succès.</p>"
                + "<div style='background: #f0f4f8; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p><strong>📚 Vous pouvez maintenant :</strong></p>"
                + "  <ul>"
                + "    <li>📖 Consulter les tutoriels</li>"
                + "    <li>📝 Passer des examens blancs</li>"
                + "    <li>📊 Suivre votre progression</li>"
                + "  </ul>"
                + "</div>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "welcome", subject, body, event.getUserEmail());
            log.info("✅ Email de bienvenue envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "welcome", subject, body, event.getUserEmail(), e.getMessage());
        }
    }

    // ─── 3. MILESTONE REACHED ───
    @KafkaListener(topics = "${kafka.topics.milestone-reached:milestone-reached}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMilestone(MilestoneEvent event) {
        log.info("📥 Reçu milestone-reached: userId={}, milestone={}%", event.getUserId(), event.getMilestone());

        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        String subject = "🎯 Félicitations ! " + event.getMilestone() + "% de progression !";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<div style='background: #dcfce7; padding: 20px; border-radius: 10px; margin: 20px 0; text-align: center;'>"
                + "  <h1 style='font-size: 48px; margin: 0;'>🎉</h1>"
                + "  <h2 style='color: #166534;'>" + event.getMilestone() + "% atteint !</h2>"
                + "  <p style='color: #166534;'>" + event.getMessage() + "</p>"
                + "</div>"
                + "<p>Continuez comme ça !</p>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "milestone", subject, body, event.getUserEmail());
            log.info("✅ Email de milestone envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "milestone", subject, body, event.getUserEmail(), e.getMessage());
        }
    }

    // ─── 4. INACTIVE USER ───
    @KafkaListener(topics = "${kafka.topics.user-inactive:user-inactive}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeInactiveUser(InactiveUserEvent event) {
        log.info("📥 Reçu user-inactive: userId={}, days={}", event.getUserId(), event.getDaysInactive());

        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        String subject = "⏰ Nous vous avons manqué !";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<p>Cela fait <strong>" + event.getDaysInactive() + " jours</strong> que vous n'avez pas été actif.</p>"
                + "<div style='background: #fef9c3; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p>📚 Ne laissez pas votre progression s'arrêter !</p>"
                + "  <p>Connectez-vous pour continuer votre formation.</p>"
                + "</div>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "reminder", subject, body, event.getUserEmail());
            log.info("✅ Email de rappel envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "reminder", subject, body, event.getUserEmail(), e.getMessage());
        }
    }
    /*

    // ─── 5. REGISTRATION PROCESSED ───
    @KafkaListener(topics = "${kafka.topics.registration-processed:registration-processed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRegistrationProcessed(RegistrationProcessedEvent event) {
        log.info("📥 Reçu registration-processed: userId={}", event.getUserId());

        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        String subject = "✅ Inscription confirmée";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<div style='background: #dcfce7; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p>✅ Votre inscription à l'auto-école a été validée.</p>"
                + "  <p>Vous pouvez maintenant commencer votre formation.</p>"
                + "</div>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "registration-confirmation", subject, body, event.getUserEmail());
            log.info("✅ Email de confirmation d'inscription envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "registration-confirmation", subject, body, event.getUserEmail(), e.getMessage());
        }
    }

    // ─── 6. REGISTRATION REJECTED ───
    @KafkaListener(topics = "${kafka.topics.registration-rejected:registration-rejected}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRegistrationRejected(RegistrationRejectedEvent event) {
        log.info("📥 Reçu registration-rejected: userId={}", event.getUserId());

        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        String subject = "❌ Inscription non validée";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<div style='background: #fee2e2; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p>❌ Votre inscription n'a pas été validée.</p>"
                + "  <p><strong>Raison :</strong> " + event.getReason() + "</p>"
                + "</div>"
                + "<p>N'hésitez pas à nous contacter pour plus d'informations.</p>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "registration-rejected", subject, body, event.getUserEmail());
            log.info("✅ Email de rejet envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "registration-rejected", subject, body, event.getUserEmail(), e.getMessage());
        }
    }

     */
    @KafkaListener(topics = "${kafka.topics.registration-events:registration-events}", groupId = "${spring.kafka.consumer.group-id}",containerFactory = "typedKafkaListenerContainerFactory")
    public void consumeRegistrationEvent(Message<Object> message) {
        Object event = message.getPayload();

        if (event instanceof RegistrationProcessedEvent processed) {
            log.info("📥 Reçu registration-processed: userId={}", processed.getUserId());
            handleRegistrationProcessed(processed);
        }
        else if (event instanceof RegistrationRejectedEvent rejected) {
            log.info("📥 Reçu registration-rejected: userId={}", rejected.getUserId());
            handleRegistrationRejected(rejected);
        }
        else if (event instanceof RegistrationSubmittedEvent submitted) {
            log.info("📥 Reçu registration-submitted: userId={}", submitted.getUserId());
            handleRegistrationSubmitted(submitted);
        }
        else {
            log.warn("⚠️ Événement registration inconnu: {}", event.getClass().getSimpleName());
        }
    }

    // ════════════════════════════════════════════════════════════════
    // MÉTHODES DE TRAITEMENT REGISTRATION (NOUVELLES)
    // ════════════════════════════════════════════════════════════════

    private void handleRegistrationProcessed(RegistrationProcessedEvent event) {
        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        String subject = "✅ Inscription confirmée";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<div style='background: #dcfce7; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p>✅ Votre inscription à l'auto-école a été validée.</p>"
                + "  <p>Vous pouvez maintenant commencer votre formation.</p>"
                + "</div>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "registration-confirmation", subject, body, event.getUserEmail());
            log.info("✅ Email de confirmation d'inscription envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "registration-confirmation", subject, body, event.getUserEmail(), e.getMessage());
        }
    }

    private void handleRegistrationRejected(RegistrationRejectedEvent event) {
        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        String subject = "❌ Inscription non validée";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>Bonjour " + fullName + ",</h2>"
                + "<div style='background: #fee2e2; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p>❌ Votre inscription n'a pas été validée.</p>"
                + "  <p><strong>Raison :</strong> " + event.getReason() + "</p>"
                + "</div>"
                + "<p>N'hésitez pas à nous contacter pour plus d'informations.</p>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "<hr style='border: none; border-top: 1px solid #e2e8f0;'>"
                + "<p style='color: #94a3b8; font-size: 12px;'>Auto-École Pro - Votre formation au permis</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(event.getUserEmail(), subject, body);
            logService.logSuccess(event.getUserId(), "EMAIL", "registration-rejected", subject, body, event.getUserEmail());
            log.info("✅ Email de rejet envoyé à {}", event.getUserEmail());
        } catch (Exception e) {
            logService.logFailure(event.getUserId(), "EMAIL", "registration-rejected", subject, body, event.getUserEmail(), e.getMessage());
        }
    }

    private void handleRegistrationSubmitted(RegistrationSubmittedEvent event) {
        String fullName = event.getUserFirstName() + " " + event.getUserLastName();

        // Email à l'admin
        String adminEmail = "admin@auto-ecole.com";
        String subject = "📋 Nouvelle demande d'inscription";
        String body = "<html>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
                + "<h1 style='color: #2563eb;'>Auto-École Pro</h1>"
                + "<h2>📋 Nouvelle demande d'inscription</h2>"
                + "<div style='background: #fef9c3; padding: 20px; border-radius: 10px; margin: 20px 0;'>"
                + "  <p><strong>👤 Élève :</strong> " + fullName + "</p>"
                + "  <p><strong>🆔 ID :</strong> " + event.getUserId() + "</p>"
                + "  <p><strong>📄 Demande :</strong> " + event.getRegistrationId() + "</p>"
                + "</div>"
                + "<p>Connectez-vous à l'interface admin pour traiter cette demande.</p>"
                + "<p>L'équipe Auto-École Pro</p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendHtmlEmail(adminEmail, subject, body);
            logService.logSuccess(String.valueOf(event.getUserId()), "EMAIL", "registration-submitted", subject, body, adminEmail);
            log.info("✅ Email de notification admin envoyé");
        } catch (Exception e) {
            logService.logFailure(String.valueOf(event.getUserId()), "EMAIL", "registration-submitted", subject, body, adminEmail, e.getMessage());
            log.error("❌ Échec d'envoi à l'admin");
        }
    }
}