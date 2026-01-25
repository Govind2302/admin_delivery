package com.foodgram.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryRequest {

    private Long deliveryPersonId; // Assign or reassign delivery person

    private String status; // picked_up, in_transit, delivered, failed_attempt
}