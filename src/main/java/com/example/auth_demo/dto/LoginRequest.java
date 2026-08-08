package com.example.auth_demo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username or Email is required")
    private String identifier; // Accepts Username OR Email

    @NotBlank(message = "Password is required")
    private String password;

    // 1. Default No-Args Constructor (Mandatory for Jackson)
    public LoginRequest() {}

    // 2. Parameterized Constructor
    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // 3. Getters and Setters (Mandatory for JSON Binding)
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}