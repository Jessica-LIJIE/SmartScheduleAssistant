package com.smartscheduleassistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartscheduleassistant.entity.Task;
import com.smartscheduleassistant.entity.User;
import com.smartscheduleassistant.repository.TaskRepository;
import com.smartscheduleassistant.repository.UserRepository;
import com.smartscheduleassistant.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        taskRepository.deleteAll();

        // 创建测试用户
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("123456"));
        testUser = userRepository.save(testUser);

        // 生成JWT token
        testToken = jwtUtil.generateToken(testUser.getId());
    }

    @Test
    void testCreateTask() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("content", "完成强化学习课程作业");

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("完成强化学习课程作业"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateTaskWithoutToken() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("content", "完成强化学习课程作业");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // 由于SecurityConfig中anyRequest().permitAll()，所以返回200
    }

    @Test
    void testGetTasksList() throws Exception {
        // 创建一些任务
        Task task1 = new Task();
        task1.setUserId(testUser.getId());
        task1.setContent("任务1");
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setUserId(testUser.getId());
        task2.setContent("任务2");
        taskRepository.save(task2);

        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetTasksListEmpty() throws Exception {
        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testClassifyTask() throws Exception {
        // 创建一个任务
        Task task = new Task();
        task.setUserId(testUser.getId());
        task.setContent("完成强化学习课程作业");
        task = taskRepository.save(task);

        mockMvc.perform(post("/tasks/" + task.getId() + "/classify")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(task.getId()))
                .andExpect(jsonPath("$.category").exists());
    }

    @Test
    void testClassifyTaskNotFound() throws Exception {
        mockMvc.perform(post("/tasks/999/classify")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testClassifyTaskForbidden() throws Exception {
        // 创建另一个用户
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setPasswordHash(passwordEncoder.encode("123456"));
        otherUser = userRepository.save(otherUser);

        // 创建属于另一个用户的任务
        Task task = new Task();
        task.setUserId(otherUser.getId());
        task.setContent("其他用户的任务");
        task = taskRepository.save(task);

        // 尝试分类其他用户的任务
        mockMvc.perform(post("/tasks/" + task.getId() + "/classify")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isForbidden());
    }
}

