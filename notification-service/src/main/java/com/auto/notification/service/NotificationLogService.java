package com.auto.notification.service;

import com.auto.notification.entity.NotificationLog;
import com.auto.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;

    public void logSuccess(String userId, String type, String channel, String subject, String content, String recipient) {
        NotificationLog log = NotificationLog.builder()
                .userId(userId)
                .type(type)
                .channel(channel)
                .subject(subject)
                .content(content)
                .recipient(recipient)
                .status("SENT")
                .sentAt(LocalDateTime.now())
                .build();
        notificationLogRepository.save(log);
    }

    public void logFailure(String userId, String type, String channel, String subject, String content, String recipient, String errorMessage) {
        NotificationLog log = NotificationLog.builder()
                .userId(userId)
                .type(type)
                .channel(channel)
                .subject(subject)
                .content(content)
                .recipient(recipient)
                .status("FAILED")
                .errorMessage(errorMessage)
                .build();
        notificationLogRepository.save(log);
    }

    public Page<NotificationLog> getLogsByUser(String userId, Pageable pageable) {
        return notificationLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<NotificationLog> getLogsByUserAndStatus(String userId, String status) {
        return notificationLogRepository.findByUserIdAndStatus(userId, status);
    }

    public long countSentByUser(String userId) {
        return notificationLogRepository.countSentByUser(userId);
    }

    public long countFailed() {
        return notificationLogRepository.countFailed();
    }
}