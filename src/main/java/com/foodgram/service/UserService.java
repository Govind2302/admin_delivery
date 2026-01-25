package com.foodgram.service;

import com.foodgram.dto.request.UpdateUserRequest;
import com.foodgram.dto.response.UserResponse;
import com.foodgram.exception.BadRequestException;
import com.foodgram.exception.ResourceNotFoundException;
import com.foodgram.model.User;
import com.foodgram.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Get all users with pagination and filters
    public Page<UserResponse> getAllUsers(int page, int size, String role, String status) {
        logger.info("Fetching users - page: {}, size: {}, role: {}, status: {}", page, size, role, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users;

        if (role != null && status != null) {
            users = userRepository.findByRoleAndStatus(
                    User.Role.valueOf(role),
                    User.Status.valueOf(status),
                    pageable
            );
        } else if (role != null) {
            users = userRepository.findByRole(User.Role.valueOf(role), pageable);
        } else if (status != null) {
            users = userRepository.findByStatus(User.Status.valueOf(status), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(this::convertToResponse);
    }

    // Get user by ID
    public UserResponse getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return convertToResponse(user);
    }

    // Update user
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        logger.info("Updating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if email is being changed and if it already exists
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Update fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(User.Role.valueOf(request.getRole()));
        }
        if (request.getStatus() != null) {
            user.setStatus(User.Status.valueOf(request.getStatus()));
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", updatedUser.getEmail());

        return convertToResponse(updatedUser);
    }

    // Delete user
    public void deleteUser(Long userId) {
        logger.info("Deleting user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        userRepository.delete(user);
        logger.info("User deleted successfully: {}", user.getEmail());
    }

    // Update user status
    public UserResponse updateUserStatus(Long userId, String status) {
        logger.info("Updating status for user ID: {} to {}", userId, status);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        try {
            user.setStatus(User.Status.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status value. Allowed: active, inactive, suspended");
        }

        User updatedUser = userRepository.save(user);
        logger.info("User status updated successfully");

        return convertToResponse(updatedUser);
    }

    // Get users by role
    public List<UserResponse> getUsersByRole(String role) {
        logger.info("Fetching users by role: {}", role);

        try {
            User.Role userRole = User.Role.valueOf(role);
            List<User> users = userRepository.findAllByRole(userRole);
            return users.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role value");
        }
    }

    // Helper method to convert User entity to UserResponse DTO
    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}