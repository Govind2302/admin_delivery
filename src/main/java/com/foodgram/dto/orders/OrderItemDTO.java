package com.foodgram.dto.orders;

import lombok.Data;

@Data
public class OrderItemDTO {
    private int itemId;
    private String name;
    private int quantity;
    private double price;
}