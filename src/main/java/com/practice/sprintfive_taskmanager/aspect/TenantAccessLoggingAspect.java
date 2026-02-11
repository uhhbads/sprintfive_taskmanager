package com.practice.sprintfive_taskmanager.aspect;

import com.practice.sprintfive_taskmanager.config.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TenantAccessLoggingAspect {
    // Pointcut for all GET mappings
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMappingPointcut() {}

    // Pointcut for all POST mappings
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMappingPointcut() {}

    // Pointcut for all PUT mappings
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMappingPointcut() {}

    // Pointcut for all DELETE mappings
    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void deleteMappingPointcut() {}

    // Combine all pointcuts
    @Pointcut("getMappingPointcut() || postMappingPointcut() || putMappingPointcut() || deleteMappingPointcut()")
    public void allMappingsPointcut() {}

    @Around("allMappingsPointcut()")
    public Object logTenantAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Long tenantId = TenantContext.getTenantId();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Tenant {} accessing {}.{}", tenantId, className, methodName);

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Tenant {} completed {}.{} in {}ms",
                    tenantId, className, methodName, duration);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;

            log.error("Tenant {} failed {}.{} after {}ms with error: {}",
                    tenantId, className, methodName, duration, e.getMessage());

            throw e;
        }
    }
}
