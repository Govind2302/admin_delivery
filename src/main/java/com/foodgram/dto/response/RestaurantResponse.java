package com.foodgram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private Long restaurantId;
    private Long userId;
    private String name;
    private String cuisineType;
    private String contactNumber;
    private String address;
    private String openTime; // Format: "HH:mm"
    private String closeTime; // Format: "HH:mm"
    private String verificationStatus;
    private BigDecimal rating;
}