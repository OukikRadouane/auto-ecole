package com.auto.auth.Service.impl;

import com.auto.auth.Event.UserDeletedEvent;
import com.auto.auth.Event.UserEmailVerifiedEvent;
import com.auto.auth.Event.UserRegisteredEvent;
import com.auto.auth.Service.UserEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventProducerImpl implements UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Value("${app.kafka.topics.user-email-verified}")
    private String userEmailVerifiedTopic;

    @Value("${app.kafka.topics.user-deleted}")
    private String userDeletedTopic;

    @Override
    public void publishUserRegistered(String userId, String email) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId(userId)
                .email(email)
                .occurredAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(userRegisteredTopic, userId, event)
                .whenComplete((result, ex) ->{
                    if (ex != null) {
                        log.error("Échec de publication user-registered pour userId={}", userId, ex);
                    } else {
                        log.info("Événement user-registered publié pour userId={}", userId);
                    }
                });
    }

    @Override
    public void publishUserEmailVerified(String userId) {
        UserEmailVerifiedEvent event = UserEmailVerifiedEvent.builder()
                .userId(userId)
                .occurredAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(userEmailVerifiedTopic, userId, event)
                .whenComplete((result, ex) ->{
                    if(ex != null){
                        log.info("Échec de publication user-email-verified pour userId={}", userId, ex);
                    } else{
                        log.info("Événement user-email-verified publié pour userId={}", userId);
                    }
                });
    }

    @Override
    public void publishUserDeleted(String userId) {
        UserDeletedEvent event = UserDeletedEvent.builder()
                .userId(userId)
                .occurredAt(LocalDateTime.now())
                .build();
        kafkaTemplate.send(userDeletedTopic, userId, event)
                .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.info("Échec de publication user-deleted pour userId={}", userId, ex);
                            } else {
                                log.info("Événement user-deleted publié pour userId={}", userId);
                            }
                        }
                    );
    }
}
