package com.foodgram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long restaurantId;
    private String restaurantName;
    private Long addressId;
    private String deliveryAddress;
    private Long paymentId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
}