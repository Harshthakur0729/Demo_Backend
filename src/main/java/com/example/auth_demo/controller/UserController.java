package com.example.auth_demo.controller;

import com.example.auth_demo.dto.ApiResponse;
import com.example.auth_demo.dto.UserProfileResponse;
import com.example.auth_demo.dto.UserUpdateRequest;
import com.example.auth_demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. Get User Profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserProfileResponse profile = userService.getUserProfile(username);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile fetched successfully", profile));
    }

    // 2. Update User Profile (Returns Updated Profile + Sets New Token in Cookie)
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request,
            HttpServletResponse response) {

        String currentUsername = authentication.getName();
        Map<String, Object> result = userService.updateUserProfile(currentUsername, request);

        // Naya token extract karke Response Cookie me set karo
        String newToken = (String) result.get("token");
        ResponseCookie cookie = ResponseCookie.from("jwtToken", newToken)
                .httpOnly(false)
                .secure(false) // Dev environment
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", result));
    }

    // 3. Delete Account (Account Delete + Cookie Clear)
    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<String>> deleteProfile(
            Authentication authentication,
            HttpServletResponse response) { // 👈 1. HttpServletResponse add kiya

        String username = authentication.getName();
        userService.deleteUserProfile(username);

        // 👈 2. Expired Cookie (maxAge = 0) banayi browser se cookie clear karne ke liye
        ResponseCookie deleteCookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(0) // 👈 maxAge(0) bolta hai ki cookie immediately remove kar do
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(new ApiResponse<>(true, "Account deleted and token cleared successfully", null));
    }
}