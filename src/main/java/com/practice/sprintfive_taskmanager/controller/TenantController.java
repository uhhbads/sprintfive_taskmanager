package com.practice.sprintfive_taskmanager.controller;

import com.practice.sprintfive_taskmanager.config.TenantContext;
import com.practice.sprintfive_taskmanager.dto.request.TenantCreateRequest;
import com.practice.sprintfive_taskmanager.dto.response.TenantResponse;
import com.practice.sprintfive_taskmanager.entity.Tenant;
import com.practice.sprintfive_taskmanager.exception.TenantNotFoundException;
import com.practice.sprintfive_taskmanager.repository.TenantRepository;
import com.practice.sprintfive_taskmanager.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    private final TenantService tenantService;
    private final TenantRepository tenantRepository;

    public TenantController(TenantService tenantService, TenantRepository tenantRepository) {
        this.tenantService = tenantService;
        this.tenantRepository = tenantRepository;
    }

    @PostMapping
    public ResponseEntity<TenantResponse> postTenant(TenantCreateRequest request){
        TenantResponse tenant = tenantService.createTenant(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tenant);
    }

    @GetMapping("/current")
    public ResponseEntity<TenantResponse> getCurrentTenant(){
        Long tenantId = TenantContext.getTenantId();

        return ResponseEntity.ok(tenantService.getTenantByTenantId(tenantId));
    }
}
