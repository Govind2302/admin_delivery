package com.foodgram.dto.orders;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private int orderId;
    private String orderStatus;
    private double totalAmount;
    private String orderDate;

    private String customerName;
    private String restaurantName;
    private List<OrderItemDTO> items;


}