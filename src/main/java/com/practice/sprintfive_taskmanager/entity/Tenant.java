package com.practice.sprintfive_taskmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tenantKey; // Unique identifier like "acme-corp"

    private String companyName;
    private String domain; // e.g., acme.com

    @Enumerated(EnumType.STRING)
    private TenantStatus status; // ACTIVE, SUSPENDED, DELETED

    private LocalDateTime createdAt;
}
