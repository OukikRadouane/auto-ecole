package com.auto.auth.Service.impl;

import com.auto.auth.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Override
    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        String link = frontendUrl + "/verify-email?token=" + token;
        String subject = "Vérifiez votre adresse email ";
        String body = buildVerificationEmailBody(link);

        sendHtmlEmail(toEmail, subject, body);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,false, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Échec de l'envoi de l'email à {}", toEmail, e);
        }
    }

    private String buildVerificationEmailBody(String link) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2> Bienvenue sur DrivenEdu</h2>
                    <p>Merci de vous être inscrit. Cliquez sur le lien ci-dessous pour vérifier votre email :</p>
                <p><a href="%s" style="background-color:#2563eb;color:white;padding:10px 20px;
                        text-decoration:none;border-radius:5px;">Vérifier mon email</a></p>
                    <p>Ce lien expire dans 24 heures.</p>
                    <p>Si vous n'êtes pas à l'origine de cette inscription, ignorez cet email.</p>
                </body>
                </html>
                """.formatted(link);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {

        String link = frontendUrl + "/reset-password?token=" + token;
        String subject = "Réinitialisation de votre mot de passe";
        String body = buildPasswordResetEmailBody(link);

        sendHtmlEmail(toEmail, subject, body);
    }

    private String buildPasswordResetEmailBody(String link) {
        return """
                <html>
                     <body style="font-family: Arial, sans-serif; color: #333;">
                          <h2>Réinitialisation de mot de passe</h2>
                          <p>Vous avez demandé la réinitialisation de votre mot de passe. Cliquez ci-dessous :</p>
                          <p><a href="%s" style="background-color:#2563eb;color:white;padding:10px 20px;
                                        text-decoration:none;border-radius:5px;">Réinitialiser mon mot de passe</a></p>
                          <p>Ce lien expire dans 30 minutes.</p>
                          <p>Si vous n'êtes pas à l'origine de cette demande, ignorez cet email — votre mot de passe reste inchangé.</p>
                     </body>
                </html>
                """.formatted(link);
    }
}
