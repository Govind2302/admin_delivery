package com.foodgram.dto.deliveryperson;


import lombok.Data;

@Data
public class DeliveryPersonDTO {
    private Long userId;               // required for linking to existing user
    private String vehicleNumber;     // required
    private String operatingArea;     // required
    private String status;              // must be "pending", "verified", or "rejected"
    private double earnings;          // optional or default to 0.0
}