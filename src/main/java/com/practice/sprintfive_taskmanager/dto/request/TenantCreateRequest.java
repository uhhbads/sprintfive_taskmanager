package com.practice.sprintfive_taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TenantCreateRequest {
    @NotBlank
    private String tenantKey;

    @NotBlank
    private String companyName;

    @NotBlank
    private String domain;
}
