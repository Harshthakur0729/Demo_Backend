package com.example.auth_demo.controller;

import com.example.auth_demo.dto.ApiResponse;
import com.example.auth_demo.dto.LoginRequest;
import com.example.auth_demo.dto.RegisterRequest;
import com.example.auth_demo.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 1. Register Endpoint
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, message, null));
    }

    // 2. Login Endpoint (Sets Cookie)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        String token = authService.loginUser(request);

        ResponseCookie cookie = ResponseCookie.from("jwtToken", token)
                .httpOnly(false)
                .secure(false) // Localhost dev setup
                .path("/")
                .maxAge(24 * 60 * 60) // 1 Day
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful!", token));
    }

    // 3. Auth Check Endpoint
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> checkAuth() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (username != null && !"anonymousUser".equals(username)) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Authenticated", username));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Not authenticated", null));
    }

    // 4. Logout Endpoint (Properly Clears Cookie)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(0) // Instantly expires cookie in browser
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, "Logout successful!", null));
    }
}