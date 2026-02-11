package com.practice.sprintfive_taskmanager.config;

import com.practice.sprintfive_taskmanager.entity.Tenant;
import com.practice.sprintfive_taskmanager.exception.MissingTenantException;
import com.practice.sprintfive_taskmanager.exception.TenantNotFoundException;
import com.practice.sprintfive_taskmanager.repository.TenantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {
    private final TenantRepository tenantRepository;

    public TenantInterceptor(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String tenantKey = request.getHeader("X-Tenant-ID");

        if (tenantKey == null) {
            throw new MissingTenantException("X-Tenant-ID header is required");
        }

        Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                .orElseThrow(() -> new TenantNotFoundException("Invalid tenant"));

        TenantContext.setTenantId(tenant.getId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
