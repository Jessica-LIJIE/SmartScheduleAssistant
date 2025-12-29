package com.smartscheduleassistant.service;

import com.smartscheduleassistant.entity.User;
import com.smartscheduleassistant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password) {
        Optional<User> exist = userRepository.findByEmail(email);
        if (exist.isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(u);
    }
}
