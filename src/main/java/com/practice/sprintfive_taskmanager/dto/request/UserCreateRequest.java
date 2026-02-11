package com.practice.sprintfive_taskmanager.dto.request;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String email;
    private String password;
    private String name;
    private String tenantKey;
}
