package com.foodgram.controller;

import com.foodgram.dto.request.LoginRequest;
import com.foodgram.dto.request.UserRegistrationRequest;
import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.LoginResponse;
import com.foodgram.dto.response.UserResponse;
import com.foodgram.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
@CrossOrigin(origins = "*")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
//        LoginResponse loginResponse = authService.adminLogin(loginRequest);
//        ApiResponse response = new ApiResponse(true, "Login successful", loginResponse);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, Object> loginResponse = authService.adminLogin(loginRequest);
        ApiResponse response = new ApiResponse(true, "Login successful", loginResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        UserResponse userResponse = authService.adminRegister(registrationRequest);
        ApiResponse response = new ApiResponse(true, "Admin registered successfully", userResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}