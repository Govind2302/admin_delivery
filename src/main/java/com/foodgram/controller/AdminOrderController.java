package com.foodgram.controller;

import com.foodgram.dto.request.UpdateOrderRequest;
import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.OrderResponse;
import com.foodgram.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // Get all orders with pagination and filters
    @GetMapping
    public ResponseEntity<ApiResponse> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long restaurantId
    ) {
        Page<OrderResponse> orders = orderService.getAllOrders(page, size, orderStatus, userId, restaurantId);
        ApiResponse response = new ApiResponse(true, "Orders fetched successfully", orders);
        return ResponseEntity.ok(response);
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        ApiResponse response = new ApiResponse(true, "Order fetched successfully", order);
        return ResponseEntity.ok(response);
    }

    // Update order (change status and/or assign delivery person)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        OrderResponse updatedOrder = orderService.updateOrder(id, request);
        ApiResponse response = new ApiResponse(true, "Order updated successfully", updatedOrder);
        return ResponseEntity.ok(response);
    }

    // Update order status only
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        ApiResponse response = new ApiResponse(true, "Order status updated successfully", updatedOrder);
        return ResponseEntity.ok(response);
    }

    // Get orders by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getOrdersByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> orders = orderService.getAllOrders(page, size, null, userId, null);
        ApiResponse response = new ApiResponse(true, "User orders fetched successfully", orders);
        return ResponseEntity.ok(response);
    }

    // Get orders by restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse> getOrdersByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderResponse> orders = orderService.getAllOrders(page, size, null, null, restaurantId);
        ApiResponse response = new ApiResponse(true, "Restaurant orders fetched successfully", orders);
        return ResponseEntity.ok(response);
    }
}