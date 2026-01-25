package com.foodgram.service;

import com.foodgram.config.JwtUtil;
import com.foodgram.dto.deliveryperson.DelLoginRequest;
import com.foodgram.dto.deliveryperson.DelRegisterRequest;
import com.foodgram.dto.request.LoginRequest;
import com.foodgram.dto.request.UserRegistrationRequest;
import com.foodgram.dto.response.LoginResponse;
import com.foodgram.dto.response.UserResponse;
import com.foodgram.exception.BadRequestException;
import com.foodgram.exception.UnauthorizedException;
import com.foodgram.model.User;
import com.foodgram.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

//    public LoginResponse adminLogin(LoginRequest loginRequest) {
//        logger.info("Login attempt for email: {}", loginRequest.getEmail());
//
//        // Find user by email and check if role is ADMIN
//        User user = userRepository.findByEmailAndRole(loginRequest.getEmail(), User.Role.admin)
//                .orElseThrow(() -> {
//                    logger.error("User not found or not an admin: {}", loginRequest.getEmail());
//                    return new UnauthorizedException("Invalid credentials or not an admin user");
//                });
//
//        logger.info("User found: {}, Role: {}, Status: {}", user.getEmail(), user.getRole(), user.getStatus());
//
//        // Check if account is active
//        if (user.getStatus() != User.Status.active) {
//            logger.error("Account is not active: {}", user.getStatus());
//            throw new UnauthorizedException("Account is " + user.getStatus().getValue());
//        }
//
//        // Verify password (plain text comparison)
//        if (!loginRequest.getPassword().equals(user.getPassword())) {
//            logger.error("Password mismatch for user: {}", user.getEmail());
//            throw new UnauthorizedException("Invalid credentials");
//        }
//
//        logger.info("Login successful for user: {}", user.getEmail());
//
//        // Return login response
//        return new LoginResponse(
//                user.getUserId(),
//                user.getFullName(),
//                user.getEmail(),
//                user.getRole().name(),
//                user.getStatus().name()
//        );
//    }
    public Map<String, Object> adminLogin(LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());

        User user = userRepository.findByEmailAndRole(loginRequest.getEmail(), User.Role.admin)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials or not an admin user"));

        if (user.getStatus() != User.Status.active) {
            throw new UnauthorizedException("Account is " + user.getStatus().getValue());
        }

        // Use BCrypt for password check if you encode admin passwords
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("status", user.getStatus().name());
        return response;
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
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Plain text password// updated new hashedpassword
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

    @Autowired
    private PasswordEncoder passwordEncoder; // BCryptPasswordEncoder

    @Autowired
    private JwtUtil jwtUtil; // utility for JWT generation

    public Map<String, Object> register(DelRegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(User.Role.valueOf(request.getRole().toLowerCase()));
        user.setStatus(User.Status.inactive);

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", user.getUserId());
        response.put("role", user.getRole());
        return response;
    }

    public Map<String, Object> login(@Valid @NotNull DelLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("role", user.getRole());
        return response;
    }



}