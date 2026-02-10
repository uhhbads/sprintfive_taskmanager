package com.practice.sprintfive_taskmanager.repository;

import com.practice.sprintfive_taskmanager.entity.Task;
import com.practice.sprintfive_taskmanager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByTenantId(Long tenantId);
    Optional<Task> findByIdAndTenantId(Long id, Long tenantId);
    List<Task> findByTenantIdAndStatus(Long tenantId, TaskStatus status);
}
