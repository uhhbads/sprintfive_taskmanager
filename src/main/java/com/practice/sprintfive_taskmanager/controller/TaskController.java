package com.practice.sprintfive_taskmanager.controller;

import com.practice.sprintfive_taskmanager.dto.request.TaskCreateRequest;
import com.practice.sprintfive_taskmanager.dto.request.TaskUpdateRequest;
import com.practice.sprintfive_taskmanager.entity.Task;
import com.practice.sprintfive_taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> postTask(
            @RequestHeader("X-Tenant-ID") String tenantKey,
            @RequestBody TaskCreateRequest request){
        Task createdTask = taskService.createTask(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(
            @RequestHeader("X-Tenant-ID") String tenantKey){
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getSpecificTask(
            @RequestHeader("X-Tenant-ID") String tenantKey,
            @PathVariable Long taskId){
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateSpecificTask(
            @RequestHeader("X-Tenant-ID") String tenantKey,
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest request){
        Task updatedTask = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteSpecificTask(
            @RequestHeader("X-Tenant-ID") String tenantKey,
            @PathVariable Long taskId){
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
