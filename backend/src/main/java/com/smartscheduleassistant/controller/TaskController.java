package com.smartscheduleassistant.controller;

import com.smartscheduleassistant.entity.Task;
import com.smartscheduleassistant.service.TaskService;
import com.smartscheduleassistant.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final LlmService llmService;

    public TaskController(TaskService taskService, LlmService llmService) { this.taskService = taskService; this.llmService = llmService; }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Task t, HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(401).body("Unauthorized");
        t.setUserId(((Number)uid).longValue());
        Task saved = taskService.create(t);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<?> listByUser(HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(401).body("Unauthorized");
        List<Task> list = taskService.listByUser(((Number)uid).longValue());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/classify")
    public ResponseEntity<?> classify(@PathVariable Long id, HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(401).body("Unauthorized");
        return taskService.findById(id).map(t -> {
            if (!t.getUserId().equals(((Number)uid).longValue())) return ResponseEntity.status(403).body("Forbidden");
            String cat = llmService.classify(t.getContent());
            t.setCategory(cat);
            taskService.create(t);
            return ResponseEntity.ok(Map.of("taskId", t.getId(), "category", cat));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(401).body("Unauthorized");
        return taskService.findById(id).map(t -> {
            if (!t.getUserId().equals(((Number)uid).longValue())) {
                return ResponseEntity.status(403).body("Forbidden");
            }
            taskService.delete(id);
            return ResponseEntity.ok(Map.of("message", "任务已删除"));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
