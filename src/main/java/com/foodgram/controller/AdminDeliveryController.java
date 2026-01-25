package com.foodgram.controller;

import com.foodgram.dto.request.UpdateDeliveryRequest;
import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.DeliveryResponse;
import com.foodgram.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/deliveries")
public class AdminDeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // Get all deliveries with pagination and filters
    @GetMapping
    public ResponseEntity<ApiResponse> getAllDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long deliveryPersonId
    ) {
        Page<DeliveryResponse> deliveries = deliveryService.getAllDeliveries(page, size, status, deliveryPersonId);
        ApiResponse response = new ApiResponse(true, "Deliveries fetched successfully", deliveries);
        return ResponseEntity.ok(response);
    }

    // Get delivery by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDeliveryById(@PathVariable Long id) {
        DeliveryResponse delivery = deliveryService.getDeliveryById(id);
        ApiResponse response = new ApiResponse(true, "Delivery fetched successfully", delivery);
        return ResponseEntity.ok(response);
    }

    // Get delivery by order ID
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse> getDeliveryByOrderId(@PathVariable Long orderId) {
        DeliveryResponse delivery = deliveryService.getDeliveryByOrderId(orderId);
        ApiResponse response = new ApiResponse(true, "Delivery fetched successfully", delivery);
        return ResponseEntity.ok(response);
    }

    // Update delivery (reassign delivery person and/or update status)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDelivery(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeliveryRequest request
    ) {
        DeliveryResponse updatedDelivery = deliveryService.updateDelivery(id, request);
        ApiResponse response = new ApiResponse(true, "Delivery updated successfully", updatedDelivery);
        return ResponseEntity.ok(response);
    }

    // Update delivery status only
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        DeliveryResponse updatedDelivery = deliveryService.updateDeliveryStatus(id, status);
        ApiResponse response = new ApiResponse(true, "Delivery status updated successfully", updatedDelivery);
        return ResponseEntity.ok(response);
    }

    // Assign delivery person
    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse> assignDeliveryPerson(
            @PathVariable Long id,
            @RequestParam Long deliveryPersonId
    ) {
        DeliveryResponse updatedDelivery = deliveryService.assignDeliveryPerson(id, deliveryPersonId);
        ApiResponse response = new ApiResponse(true, "Delivery person assigned successfully", updatedDelivery);
        return ResponseEntity.ok(response);
    }

    // Get deliveries by delivery person
    @GetMapping("/delivery-person/{deliveryPersonId}")
    public ResponseEntity<ApiResponse> getDeliveriesByDeliveryPerson(@PathVariable Long deliveryPersonId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesByDeliveryPerson(deliveryPersonId);
        ApiResponse response = new ApiResponse(true, "Deliveries fetched successfully", deliveries);
        return ResponseEntity.ok(response);
    }

    // Get active deliveries
    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveDeliveries() {
        List<DeliveryResponse> deliveries = deliveryService.getActiveDeliveries();
        ApiResponse response = new ApiResponse(true, "Active deliveries fetched successfully", deliveries);
        return ResponseEntity.ok(response);
    }
}