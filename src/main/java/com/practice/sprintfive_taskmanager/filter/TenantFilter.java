package com.practice.sprintfive_taskmanager.filter;

import com.practice.sprintfive_taskmanager.config.TenantContext;
import com.practice.sprintfive_taskmanager.entity.Tenant;
import com.practice.sprintfive_taskmanager.exception.TenantNotFoundException;
import com.practice.sprintfive_taskmanager.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {
    private final TenantRepository tenantRepository;

    public TenantFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String tenantKey = request.getHeader("X-Tenant-ID");

        if(tenantKey != null){
            Tenant tenant = tenantRepository.findByTenantKey(tenantKey)
                    .orElseThrow(() -> new TenantNotFoundException("Invalid tenant"));
            TenantContext.setTenantId(tenant.getId());
        }

        try{
            filterChain.doFilter(request,response);
        } finally {
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        // Skip filter for tenant registration endpoint
        return path.equals("/api/tenants") && method.equals("POST");
    }
}
