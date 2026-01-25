package com.foodgram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long reviewId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long restaurantId;
    private String restaurantName;
    private Long itemId;
    private String itemName;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private BigDecimal rating;
    private String comment;
    private LocalDateTime createdAt;
    private String reviewType; // "restaurant", "item", "delivery"
}