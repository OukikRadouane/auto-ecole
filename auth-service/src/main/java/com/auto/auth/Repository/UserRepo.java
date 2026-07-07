package com.auto.auth.Repository;

import com.auto.auth.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,String> {

    User findByEmail(String email);
}
