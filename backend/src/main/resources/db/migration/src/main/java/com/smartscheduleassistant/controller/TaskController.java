package com.smartscheduleassistant.controller;

import com.smartscheduleassistant.entity.Task;
import com.smartscheduleassistant.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task t) {
        Task saved = taskService.create(t);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Task>> listByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(taskService.listByUser(userId));
    }
}
