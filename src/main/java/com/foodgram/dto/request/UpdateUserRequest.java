package com.foodgram.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String role; // customer, restaurant_owner, delivery_person, admin

    private String status; // active, inactive, suspended
}