package com.example.auth_demo.service;

import com.example.auth_demo.dto.UserProfileResponse;
import com.example.auth_demo.dto.UserUpdateRequest;
import com.example.auth_demo.entity.User;
import com.example.auth_demo.exception.ResourceNotFoundException;
import com.example.auth_demo.exception.UserAlreadyExistsException;
import com.example.auth_demo.repository.UserRepository;
import com.example.auth_demo.security.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    // 1. Get Profile
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username/email: " + username));

        return new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    // 2. Update Profile & Regenerate JWT
    @Transactional
    public Map<String, Object> updateUserProfile(String currentUsername, UserUpdateRequest request) {
        User user = userRepository.findByUsernameOrEmail(currentUsername, currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + currentUsername));

        // Username update check
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()
                && !request.getUsername().equalsIgnoreCase(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken!");
            }
            user.setUsername(request.getUsername());
        }

        // Email update check
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()
                && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered!");
            }
            user.setEmail(request.getEmail());
        }

        User updatedUser = userRepository.save(user);

        // Generate updated token with new username
        String newToken = jwtUtils.generateToken(updatedUser.getUsername());

        UserProfileResponse profileResponse = new UserProfileResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail()
        );

        return Map.of(
                "profile", profileResponse,
                "token", newToken
        );
    }

    // 3. Delete Profile
    @Transactional
    public void deleteUserProfile(String username) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        userRepository.delete(user);
    }
}