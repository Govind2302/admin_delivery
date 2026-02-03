package com.foodgram.controller;

import com.foodgram.dto.request.UpdateUserRequest;
import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.UserResponse;
import com.foodgram.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminUserController {

    @Autowired
    private UserService userService;

    // Get all users with pagination and filters
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        Page<UserResponse> users = userService.getAllUsers(page, size, role, status);
        ApiResponse response = new ApiResponse(true, "Users fetched successfully", users);
        return ResponseEntity.ok(response);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        ApiResponse response = new ApiResponse(true, "User fetched successfully", user);
        return ResponseEntity.ok(response);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse updatedUser = userService.updateUser(id, request);
        ApiResponse response = new ApiResponse(true, "User updated successfully", updatedUser);
        return ResponseEntity.ok(response);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse response = new ApiResponse(true, "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Update user status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        UserResponse updatedUser = userService.updateUserStatus(id, status);
        ApiResponse response = new ApiResponse(true, "User status updated successfully", updatedUser);
        return ResponseEntity.ok(response);
    }

    // Get users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse> getUsersByRole(@PathVariable String role) {
        List<UserResponse> users = userService.getUsersByRole(role);
        ApiResponse response = new ApiResponse(true, "Users fetched successfully", users);
        return ResponseEntity.ok(response);
    }
}