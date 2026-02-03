package com.foodgram.dto.orders;

import lombok.Data;

@Data
public class DeliveryOrderDTO {
    private int deliveryId;
    private int deliveryPersonId;
    private String deliveryStatus;
    private String deliveryTime;

    private OrderDTO order;
}