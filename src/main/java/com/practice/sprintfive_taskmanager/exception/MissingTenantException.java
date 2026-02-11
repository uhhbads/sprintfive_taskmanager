package com.practice.sprintfive_taskmanager.exception;

public class MissingTenantException extends RuntimeException {
    public MissingTenantException(String message) {
        super(message);
    }
}
