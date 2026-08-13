package com.auto.notification.repository;

import com.auto.notification.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    Page<NotificationLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<NotificationLog> findByUserIdAndStatus(String userId, String status);

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.userId = :userId AND n.status = 'SENT'")
    long countSentByUser(@Param("userId") String userId);

    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.status = 'FAILED'")
    long countFailed();
}