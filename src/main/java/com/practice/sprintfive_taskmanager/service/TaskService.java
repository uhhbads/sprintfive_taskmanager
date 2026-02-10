package com.practice.sprintfive_taskmanager.service;

import com.practice.sprintfive_taskmanager.dto.request.TaskCreateRequest;
import com.practice.sprintfive_taskmanager.dto.request.TaskUpdateRequest;
import com.practice.sprintfive_taskmanager.entity.Task;
import com.practice.sprintfive_taskmanager.entity.TaskStatus;
import com.practice.sprintfive_taskmanager.entity.Tenant;
import com.practice.sprintfive_taskmanager.repository.TaskRepository;
import com.practice.sprintfive_taskmanager.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService extends BaseTenantService{
    private final TaskRepository taskRepository;
    private final TenantRepository tenantRepository;

    public TaskService(TaskRepository taskRepository, TenantRepository tenantRepository) {
        this.taskRepository = taskRepository;
        this.tenantRepository = tenantRepository;
    }

    public Task createTask(TaskCreateRequest request){
        Long tenantId = getCurrentTenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException("Invalid tenant"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTenant(request.getTenant());
        task.setAssignedTo(request.getAssignedTo());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(){
        Long tenantId = getCurrentTenantId();
        return taskRepository.findByTenantId(tenantId);
    }

    public Task getTaskById(Long id){
        Long tenantId = getCurrentTenantId();
        return taskRepository.findByIdAndTenantId(id,tenantId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    public Task updateTask(Long id, TaskUpdateRequest request){
        Task task = getTaskById(id);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssignedTo(request.getAssignedTo());
        task.setStatus(request.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id){
        Long tenantId = getCurrentTenantId();
        Task task = taskRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }
}
