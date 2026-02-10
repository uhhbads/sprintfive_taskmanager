package com.practice.sprintfive_taskmanager.service;

import com.practice.sprintfive_taskmanager.config.TenantContext;

public abstract class BaseTenantService {
    protected Long getCurrentTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new TenantContextException("No tenant context available");
        }
        return tenantId;
    }

    protected void validateTenantAccess(Long entityTenantId) {
        Long currentTenantId = getCurrentTenantId();
        if (!currentTenantId.equals(entityTenantId)) {
            throw new UnauthorizedTenantAccessException("Cannot access other tenant's data");
        }
    }
}
