package com.foodgram.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryPersonRequest {

    private String vehicleNumber;

    private String operatingArea;

    private String verificationStatus; // pending, verified, rejected
}