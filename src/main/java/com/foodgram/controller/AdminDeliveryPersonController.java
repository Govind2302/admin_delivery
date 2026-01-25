package com.foodgram.controller;

import com.foodgram.dto.deliveryperson.DeliveryPersonDTO;
import com.foodgram.dto.request.UpdateDeliveryPersonRequest;
import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.DeliveryPersonResponse;
import com.foodgram.service.DeliveryPersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/delivery-persons")
public class AdminDeliveryPersonController {

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    // Get all delivery persons with pagination and filters
    @GetMapping
    public ResponseEntity<ApiResponse> getAllDeliveryPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) String operatingArea
    ) {
        Page<DeliveryPersonResponse> deliveryPersons = deliveryPersonService.getAllDeliveryPersons(
                page, size, verificationStatus, operatingArea);
        ApiResponse response = new ApiResponse(true, "Delivery persons fetched successfully", deliveryPersons);
        return ResponseEntity.ok(response);
    }

    // Get delivery person by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDeliveryPersonById(@PathVariable Long id) {
        DeliveryPersonResponse deliveryPerson = deliveryPersonService.getDeliveryPersonById(id);
        ApiResponse response = new ApiResponse(true, "Delivery person fetched successfully", deliveryPerson);
        return ResponseEntity.ok(response);
    }

    // Get delivery person by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getDeliveryPersonByUserId(@PathVariable Long userId) {
        DeliveryPersonResponse deliveryPerson = deliveryPersonService.getDeliveryPersonByUserId(userId);
        ApiResponse response = new ApiResponse(true, "Delivery person fetched successfully", deliveryPerson);
        return ResponseEntity.ok(response);
    }

    // Update delivery person
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDeliveryPerson(
            @PathVariable int id,
            @Valid @RequestBody DeliveryPersonDTO request
    ) {
        DeliveryPersonResponse updatedDeliveryPerson = deliveryPersonService.updateDeliveryPerson(id, request);
        ApiResponse response = new ApiResponse(true, "Delivery person updated successfully", updatedDeliveryPerson);
        return ResponseEntity.ok(response);
    }

    // Delete delivery person
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDeliveryPerson(@PathVariable Long id) {
        deliveryPersonService.deleteDeliveryPerson(id);
        ApiResponse response = new ApiResponse(true, "Delivery person deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Update verification status
    @PatchMapping("/{id}/verification")
    public ResponseEntity<ApiResponse> updateVerificationStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        DeliveryPersonResponse updatedDeliveryPerson = deliveryPersonService.updateVerificationStatus(id, status);
        ApiResponse response = new ApiResponse(true, "Verification status updated successfully", updatedDeliveryPerson);
        return ResponseEntity.ok(response);
    }

    // Get pending verification delivery persons
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPendingDeliveryPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<DeliveryPersonResponse> deliveryPersons = deliveryPersonService.getPendingDeliveryPersons(page, size);
        ApiResponse response = new ApiResponse(true, "Pending delivery persons fetched successfully", deliveryPersons);
        return ResponseEntity.ok(response);
    }
}