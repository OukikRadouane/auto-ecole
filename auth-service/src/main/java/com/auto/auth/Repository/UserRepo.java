package com.auto.auth.Repository;

import com.auto.auth.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    boolean existsByEmail(String email);
}
