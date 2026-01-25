package com.foodgram.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantRequest {

    private String name;

    private String cuisineType;

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    private String contactNumber;

    private String address;

    private String openTime; // Format: "HH:mm" e.g., "09:00"

    private String closeTime; // Format: "HH:mm" e.g., "22:00"

    private String verificationStatus; // pending, verified, rejected
}