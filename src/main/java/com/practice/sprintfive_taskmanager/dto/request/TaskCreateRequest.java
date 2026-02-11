package com.practice.sprintfive_taskmanager.dto.request;

import com.practice.sprintfive_taskmanager.entity.TaskStatus;
import com.practice.sprintfive_taskmanager.entity.Tenant;
import com.practice.sprintfive_taskmanager.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private User assignedTo;

    @NotNull
    private TaskStatus status;
}
