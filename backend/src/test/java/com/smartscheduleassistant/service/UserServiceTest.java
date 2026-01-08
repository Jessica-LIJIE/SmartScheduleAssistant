package com.smartscheduleassistant.service;

import com.smartscheduleassistant.entity.User;
import com.smartscheduleassistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() {
        User user = userService.register("test@example.com", "123456");
        
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertTrue(passwordEncoder.matches("123456", user.getPasswordHash()));
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testRegisterDuplicateEmail() {
        userService.register("test@example.com", "123456");
        
        assertThrows(RuntimeException.class, () -> {
            userService.register("test@example.com", "654321");
        });
    }

    @Test
    void testFindByEmail() {
        User user = userService.register("test@example.com", "123456");
        
        assertTrue(userService.findByEmail("test@example.com").isPresent());
        assertEquals(user.getId(), userService.findByEmail("test@example.com").get().getId());
        assertTrue(userService.findByEmail("nonexistent@example.com").isEmpty());
    }
}

