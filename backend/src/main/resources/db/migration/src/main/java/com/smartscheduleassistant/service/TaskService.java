package com.smartscheduleassistant.service;

import com.smartscheduleassistant.entity.Task;
import com.smartscheduleassistant.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task t) { return taskRepository.save(t); }

    public List<Task> listByUser(Long userId) { return taskRepository.findByUserId(userId); }
}
