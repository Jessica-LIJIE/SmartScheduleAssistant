package com.smartscheduleassistant.service;

import com.smartscheduleassistant.entity.Task;
import com.smartscheduleassistant.entity.User;
import com.smartscheduleassistant.repository.TaskRepository;
import com.smartscheduleassistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("123456"));
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateTask() {
        Task task = new Task();
        task.setUserId(testUser.getId());
        task.setContent("完成强化学习课程作业");
        
        Task saved = taskService.create(task);
        
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(testUser.getId(), saved.getUserId());
        assertEquals("完成强化学习课程作业", saved.getContent());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testListByUser() {
        // 创建多个任务
        Task task1 = new Task();
        task1.setUserId(testUser.getId());
        task1.setContent("任务1");
        taskService.create(task1);

        Task task2 = new Task();
        task2.setUserId(testUser.getId());
        task2.setContent("任务2");
        taskService.create(task2);

        // 创建另一个用户的任务
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setPasswordHash(passwordEncoder.encode("123456"));
        otherUser = userRepository.save(otherUser);

        Task task3 = new Task();
        task3.setUserId(otherUser.getId());
        task3.setContent("其他用户的任务");
        taskService.create(task3);

        // 查询测试用户的任务
        List<Task> tasks = taskService.listByUser(testUser.getId());
        
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getUserId().equals(testUser.getId())));
    }

    @Test
    void testFindById() {
        Task task = new Task();
        task.setUserId(testUser.getId());
        task.setContent("测试任务");
        Task saved = taskService.create(task);

        assertTrue(taskService.findById(saved.getId()).isPresent());
        assertEquals(saved.getId(), taskService.findById(saved.getId()).get().getId());
        assertTrue(taskService.findById(999L).isEmpty());
    }
}

