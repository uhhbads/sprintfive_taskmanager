package com.practice.sprintfive_taskmanager.exception;

public class DuplicateTenantKeyException extends RuntimeException {
    public DuplicateTenantKeyException(String message) {
        super(message);
    }
}
