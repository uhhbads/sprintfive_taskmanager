package com.practice.sprintfive_taskmanager.service;

import com.practice.sprintfive_taskmanager.dto.request.TenantCreateRequest;
import com.practice.sprintfive_taskmanager.dto.response.TenantResponse;
import com.practice.sprintfive_taskmanager.entity.Tenant;
import com.practice.sprintfive_taskmanager.entity.TenantStatus;
import com.practice.sprintfive_taskmanager.repository.TenantRepository;

import java.time.LocalDateTime;

public class TenantService extends BaseTenantService{
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantResponse createTenant(TenantCreateRequest request){
        Tenant existingTenant = tenantRepository.findByTenantKey(request.getTenantKey())
                .ifPresent(t -> {
                    throw new DuplicateTenantKeyException("Tenant key already exists: " + request.getTenantKey());
                });

        Tenant tenant = new Tenant();

        tenant.setTenantKey(request.getTenantKey());
        tenant.setCompanyName(request.getCompanyName());
        tenant.setDomain(request.getDomain());

        tenant.setStatus(TenantStatus.ACTIVE);
        tenant.setCreatedAt(LocalDateTime.now());

        tenantRepository.save(tenant);
        return mapToTenantResponse(tenant);
    }

    public TenantResponse getTenantByKey(String key){

    }

    public TenantResponse updateTenantStatus(Long id, TenantStatus status){

    }

    private TenantResponse mapToTenantResponse(Tenant tenant){
        TenantResponse response = new TenantResponse();

        response.setTenantKey(tenant.getTenantKey());
        response.setCompanyName(tenant.getCompanyName());
        response.setDomain(tenant.getDomain());
        response.setStatus(tenant.getStatus());
        response.setCreatedAt(tenant.getCreatedAt());

        return response;
    }
}
