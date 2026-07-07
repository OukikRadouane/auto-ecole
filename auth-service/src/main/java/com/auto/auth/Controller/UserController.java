package com.auto.auth.Controller;

import com.auto.auth.Entity.User;
import com.auto.auth.Repository.UserRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserRepo userRepo;

    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    @PostMapping("/login")
    public String login(@RequestBody String email,String password){
        User user = userRepo.findByEmail(email);
        return "";
    }
}
