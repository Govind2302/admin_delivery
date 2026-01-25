package com.foodgram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {

    private Long deliveryId;
    private Long orderId;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private String deliveryPersonPhone;
    private String deliveryPersonVehicle;
    private String status;
    private LocalDateTime deliveryTime;

    // Order related info
    private String customerName;
    private String restaurantName;
    private String deliveryAddress;
}