package com.auto.auth.Service;

import org.springframework.stereotype.Service;

public interface UserEventProducer {
    void publishUserRegistered(String userId, String email);
    void publishUserEmailVerified(String userId);

    void publishUserDeleted(String userId);
}
