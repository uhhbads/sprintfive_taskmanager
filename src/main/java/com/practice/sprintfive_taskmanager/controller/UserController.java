package com.practice.sprintfive_taskmanager.controller;

import com.practice.sprintfive_taskmanager.dto.request.UserCreateRequest;
import com.practice.sprintfive_taskmanager.entity.User;
import com.practice.sprintfive_taskmanager.repository.UserRepository;
import com.practice.sprintfive_taskmanager.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final TenantService tenantService;

    public UserController(UserRepository userRepository, TenantService tenantService) {
        this.userRepository = userRepository;
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<User> postUser(
            @RequestBody UserCreateRequest request){
        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setTenant(
                tenantService.getRawTenantByKey(
                        request.getTenantKey()
                )
        );
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }
}
