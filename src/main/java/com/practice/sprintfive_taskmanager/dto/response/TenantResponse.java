package com.practice.sprintfive_taskmanager.dto.response;

import com.practice.sprintfive_taskmanager.entity.TenantStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantResponse {
    private String tenantKey;
    private String companyName;
    private String domain;

    @Enumerated(EnumType.STRING)
    private TenantStatus status;

    private LocalDateTime createdAt;
}
