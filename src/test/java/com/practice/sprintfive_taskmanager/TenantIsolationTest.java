package com.practice.sprintfive_taskmanager;

import com.practice.sprintfive_taskmanager.dto.request.TaskCreateRequest;
import com.practice.sprintfive_taskmanager.dto.request.TaskUpdateRequest;
import com.practice.sprintfive_taskmanager.dto.request.TenantCreateRequest;
import com.practice.sprintfive_taskmanager.dto.response.TenantResponse;
import com.practice.sprintfive_taskmanager.entity.Task;
import com.practice.sprintfive_taskmanager.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TenantIsolationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;
    private String baseUrl;
    private String tenantAKey;
    private String tenantBKey;
    private Long taskAId;
    private Long taskBId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api";
        restClient = RestClient.create();

        // Generate unique tenant keys for each test run
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        tenantAKey = "tenant-a-" + uniqueId;
        tenantBKey = "tenant-b-" + uniqueId;

        // Create Tenant A
        TenantCreateRequest tenantA = new TenantCreateRequest();
        tenantA.setTenantKey(tenantAKey);
        tenantA.setCompanyName("Company A");
        tenantA.setDomain("company-a.com");

        restClient.post()
                .uri(baseUrl + "/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tenantA)
                .retrieve()
                .toEntity(TenantResponse.class);

        // Create Tenant B
        TenantCreateRequest tenantB = new TenantCreateRequest();
        tenantB.setTenantKey(tenantBKey);
        tenantB.setCompanyName("Company B");
        tenantB.setDomain("company-b.com");

        restClient.post()
                .uri(baseUrl + "/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tenantB)
                .retrieve()
                .toEntity(TenantResponse.class);

        // Create task for Tenant A
        TaskCreateRequest taskForA = new TaskCreateRequest();
        taskForA.setTitle("Task for Tenant A");
        taskForA.setDescription("This belongs to Tenant A");
        taskForA.setStatus(TaskStatus.PENDING);

        ResponseEntity<Task> responseA = restClient.post()
                .uri(baseUrl + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", tenantAKey)
                .body(taskForA)
                .retrieve()
                .toEntity(Task.class);
        taskAId = responseA.getBody().getId();

        // Create task for Tenant B
        TaskCreateRequest taskForB = new TaskCreateRequest();
        taskForB.setTitle("Task for Tenant B");
        taskForB.setDescription("This belongs to Tenant B");
        taskForB.setStatus(TaskStatus.IN_PROGRESS);

        ResponseEntity<Task> responseB = restClient.post()
                .uri(baseUrl + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Tenant-ID", tenantBKey)
                .body(taskForB)
                .retrieve()
                .toEntity(Task.class);
        taskBId = responseB.getBody().getId();
    }

    @Test
    void testTasksCreatedByTenantA_NotVisibleToTenantB() {
        // Tenant B tries to access Tenant A's task
        assertThrows(RestClientResponseException.class, () -> {
            restClient.get()
                    .uri(baseUrl + "/tasks/" + taskAId)
                    .header("X-Tenant-ID", tenantBKey)
                    .retrieve()
                    .toEntity(Task.class);
        });
    }

    @Test
    void testTenantA_CanAccessOwnTask() {
        ResponseEntity<Task> response = restClient.get()
                .uri(baseUrl + "/tasks/" + taskAId)
                .header("X-Tenant-ID", tenantAKey)
                .retrieve()
                .toEntity(Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Task for Tenant A");
    }

    @Test
    void testCannotUpdateAnotherTenantsTask() {
        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Hacked Title");
        updateRequest.setDescription("This should not work");
        updateRequest.setStatus(TaskStatus.COMPLETED);

        // Tenant B tries to update Tenant A's task
        assertThrows(RestClientResponseException.class, () -> {
            restClient.put()
                    .uri(baseUrl + "/tasks/" + taskAId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Tenant-ID", tenantBKey)
                    .body(updateRequest)
                    .retrieve()
                    .toEntity(Task.class);
        });

        // Verify task was NOT updated
        ResponseEntity<Task> verifyResponse = restClient.get()
                .uri(baseUrl + "/tasks/" + taskAId)
                .header("X-Tenant-ID", tenantAKey)
                .retrieve()
                .toEntity(Task.class);

        assertThat(verifyResponse.getBody().getTitle()).isEqualTo("Task for Tenant A");
    }

    @Test
    void testCannotDeleteAnotherTenantsTask() {
        // Tenant B tries to delete Tenant A's task
        assertThrows(RestClientResponseException.class, () -> {
            restClient.delete()
                    .uri(baseUrl + "/tasks/" + taskAId)
                    .header("X-Tenant-ID", tenantBKey)
                    .retrieve()
                    .toBodilessEntity();
        });

        // Verify task still exists
        ResponseEntity<Task> verifyResponse = restClient.get()
                .uri(baseUrl + "/tasks/" + taskAId)
                .header("X-Tenant-ID", tenantAKey)
                .retrieve()
                .toEntity(Task.class);

        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testTenantContextIsClearedBetweenRequests() {
        // Request as Tenant A
        ResponseEntity<Task> responseA = restClient.get()
                .uri(baseUrl + "/tasks/" + taskAId)
                .header("X-Tenant-ID", tenantAKey)
                .retrieve()
                .toEntity(Task.class);
        assertThat(responseA.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Request as Tenant B
        ResponseEntity<Task> responseB = restClient.get()
                .uri(baseUrl + "/tasks/" + taskBId)
                .header("X-Tenant-ID", tenantBKey)
                .retrieve()
                .toEntity(Task.class);

        assertThat(responseB.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseB.getBody().getTitle()).isEqualTo("Task for Tenant B");

        // Verify cross-access fails
        assertThrows(RestClientResponseException.class, () -> {
            restClient.get()
                    .uri(baseUrl + "/tasks/" + taskAId)
                    .header("X-Tenant-ID", tenantBKey)
                    .retrieve()
                    .toEntity(Task.class);
        });
    }
}