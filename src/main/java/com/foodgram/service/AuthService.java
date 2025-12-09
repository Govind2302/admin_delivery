package com.foodgram.service;

import com.foodgram.dto.request.LoginRequest;
import com.foodgram.dto.request.UserRegistrationRequest;
import com.foodgram.dto.response.LoginResponse;
import com.foodgram.dto.response.UserResponse;
import com.foodgram.exception.BadRequestException;
import com.foodgram.exception.UnauthorizedException;
import com.foodgram.model.User;
import com.foodgram.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    public LoginResponse adminLogin(LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());

        // Find user by email and check if role is ADMIN
        User user = userRepository.findByEmailAndRole(loginRequest.getEmail(), User.Role.admin)
                .orElseThrow(() -> {
                    logger.error("User not found or not an admin: {}", loginRequest.getEmail());
                    return new UnauthorizedException("Invalid credentials or not an admin user");
                });

        logger.info("User found: {}, Role: {}, Status: {}", user.getEmail(), user.getRole(), user.getStatus());

        // Check if account is active
        if (user.getStatus() != User.Status.active) {
            logger.error("Account is not active: {}", user.getStatus());
            throw new UnauthorizedException("Account is " + user.getStatus().getValue());
        }

        // Verify password (plain text comparison)
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            logger.error("Password mismatch for user: {}", user.getEmail());
            throw new UnauthorizedException("Invalid credentials");
        }

        logger.info("Login successful for user: {}", user.getEmail());

        // Return login response
        return new LoginResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }

    public UserResponse adminRegister(UserRegistrationRequest request) {
        logger.info("Registration attempt for email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.error("Email already exists: {}", request.getEmail());
            throw new BadRequestException("Email already registered");
        }

        // Create new admin user
        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword()); // Plain text password
        newUser.setPhone(request.getPhone());
        newUser.setRole(User.Role.admin);
        newUser.setStatus(User.Status.active);
        newUser.setCreatedAt(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(newUser);
        logger.info("Admin user registered successfully: {}", savedUser.getEmail());

        // Return user response
        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole().name(),
                savedUser.getStatus().name(),
                savedUser.getCreatedAt()
        );
    }
}