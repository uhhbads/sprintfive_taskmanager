package com.practice.sprintfive_taskmanager.filter;

import com.practice.sprintfive_taskmanager.config.TenantContext;
import com.practice.sprintfive_taskmanager.entity.Tenant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String tenantKey = request.getHeader("X-Tenant-ID");

        if(tenantKey != null){
            Tenant tenant = tenantRespository.findByTenantKey(tenantKey)
                    .orElseThrow(() -> new TenantNotFoundException("Invalid tenant"));
            TenantContext.setTenantId(tenant.getId());
        }

        try{
            filterChain.doFilter(request,response);
        } finally {
            TenantContext.clear();
        }
    }
}
