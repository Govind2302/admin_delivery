package com.foodgram.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {

    private String orderStatus; // pending, confirmed, preparing, out_for_delivery, delivered, cancelled

    private Long deliveryPersonId; // Assign delivery person
}