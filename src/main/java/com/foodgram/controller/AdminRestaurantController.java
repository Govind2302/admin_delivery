package com.foodgram.controller;

import com.foodgram.dto.request.UpdateRestaurantRequest;
import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.RestaurantResponse;
import com.foodgram.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // Get all restaurants with pagination and filters
    @GetMapping
    public ResponseEntity<ApiResponse> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) String cuisineType
    ) {
        Page<RestaurantResponse> restaurants = restaurantService.getAllRestaurants(
                page, size, verificationStatus, cuisineType);
        ApiResponse response = new ApiResponse(true, "Restaurants fetched successfully", restaurants);
        return ResponseEntity.ok(response);
    }

    // Get restaurant by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse restaurant = restaurantService.getRestaurantById(id);
        ApiResponse response = new ApiResponse(true, "Restaurant fetched successfully", restaurant);
        return ResponseEntity.ok(response);
    }

    // Get restaurant by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getRestaurantByUserId(@PathVariable Long userId) {
        RestaurantResponse restaurant = restaurantService.getRestaurantByUserId(userId);
        ApiResponse response = new ApiResponse(true, "Restaurant fetched successfully", restaurant);
        return ResponseEntity.ok(response);
    }

    // Update restaurant
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRestaurantRequest request
    ) {
        RestaurantResponse updatedRestaurant = restaurantService.updateRestaurant(id, request);
        ApiResponse response = new ApiResponse(true, "Restaurant updated successfully", updatedRestaurant);
        return ResponseEntity.ok(response);
    }

    // Delete restaurant
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        ApiResponse response = new ApiResponse(true, "Restaurant deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Update verification status
    @PatchMapping("/{id}/verification")
    public ResponseEntity<ApiResponse> updateVerificationStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        RestaurantResponse updatedRestaurant = restaurantService.updateVerificationStatus(id, status);
        ApiResponse response = new ApiResponse(true, "Verification status updated successfully", updatedRestaurant);
        return ResponseEntity.ok(response);
    }

    // Update rating
    @PatchMapping("/{id}/rating")
    public ResponseEntity<ApiResponse> updateRating(
            @PathVariable Long id,
            @RequestParam BigDecimal rating
    ) {
        RestaurantResponse updatedRestaurant = restaurantService.updateRating(id, rating);
        ApiResponse response = new ApiResponse(true, "Rating updated successfully", updatedRestaurant);
        return ResponseEntity.ok(response);
    }

    // Get pending verification restaurants
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPendingRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getPendingRestaurants();
        ApiResponse response = new ApiResponse(true, "Pending restaurants fetched successfully", restaurants);
        return ResponseEntity.ok(response);
    }
}