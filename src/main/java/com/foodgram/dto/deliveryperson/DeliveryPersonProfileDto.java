package com.foodgram.dto.deliveryperson;


import lombok.Data;

@Data
public class DeliveryPersonProfileDto {
    private Long deliveryPersonId;   // unique ID for delivery person
    private Long userId;             // link to associated user
    private String fullName;         // from User
    private String email;            // from User
    private String phone;            // from User
    private String vehicleNumber;    // delivery person’s vehicle info
    private String operatingArea;    // area of operation
    private String status;           // pending / verified / rejected / inactive
    private double earnings;         // optional, defaults to 0.0
}