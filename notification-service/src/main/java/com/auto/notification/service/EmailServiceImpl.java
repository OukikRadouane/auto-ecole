package com.auto.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username:no-reply@auto-ecole.com}")
    private String FROM_EMAIL ;

    //private static final String FROM_EMAIL = "no-reply@auto-ecole.com";

    @Override
    @Async
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(FROM_EMAIL);

            mailSender.send(message);
            log.info("📧 Email simple envoyé à {}", to);
        } catch (MailException e) {
            log.error("❌ Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage());
            throw new RuntimeException("Échec de l'envoi de l'email", e);
        }
    }

    @Override
    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom(FROM_EMAIL);

            mailSender.send(message);
            log.info("📧 Email HTML envoyé à {}", to);
        } catch (MessagingException | MailException e) {
            log.error("❌ Erreur lors de l'envoi de l'email HTML à {}: {}", to, e.getMessage());
            throw new RuntimeException("Échec de l'envoi de l'email HTML", e);
        }
    }
}