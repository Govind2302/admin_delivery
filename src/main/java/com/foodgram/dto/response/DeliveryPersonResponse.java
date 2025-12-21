package com.foodgram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonResponse {

    private Long deliveryPersonId;
    private Long userId;
    private String userName; // From User table
    private String userEmail; // From User table
    private String vehicleNumber;
    private String operatingArea;
    private String verificationStatus;
    private BigDecimal earnings;
}