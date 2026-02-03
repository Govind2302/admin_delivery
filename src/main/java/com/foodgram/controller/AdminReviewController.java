package com.foodgram.controller;

import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.ReviewResponse;
import com.foodgram.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/reviews")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    // Get all reviews with pagination and filters
    @GetMapping
    public ResponseEntity<ApiResponse> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long deliveryPersonId
    ) {
        Page<ReviewResponse> reviews = reviewService.getAllReviews(page, size, restaurantId, userId, deliveryPersonId);
        ApiResponse response = new ApiResponse(true, "Reviews fetched successfully", reviews);
        return ResponseEntity.ok(response);
    }

    // Get review by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getReviewById(@PathVariable Long id) {
        ReviewResponse review = reviewService.getReviewById(id);
        ApiResponse response = new ApiResponse(true, "Review fetched successfully", review);
        return ResponseEntity.ok(response);
    }

    // Get reviews by restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse> getReviewsByRestaurant(@PathVariable Long restaurantId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByRestaurant(restaurantId);
        ApiResponse response = new ApiResponse(true, "Restaurant reviews fetched successfully", reviews);
        return ResponseEntity.ok(response);
    }

    // Get reviews by delivery person
    @GetMapping("/delivery-person/{deliveryPersonId}")
    public ResponseEntity<ApiResponse> getReviewsByDeliveryPerson(@PathVariable Long deliveryPersonId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByDeliveryPerson(deliveryPersonId);
        ApiResponse response = new ApiResponse(true, "Delivery person reviews fetched successfully", reviews);
        return ResponseEntity.ok(response);
    }

    // Get average ratings
    @GetMapping("/restaurant/{restaurantId}/rating")
    public ResponseEntity<ApiResponse> getRestaurantRating(@PathVariable Long restaurantId) {
        Double avgRating = reviewService.getRestaurantAverageRating(restaurantId);
        Map<String, Object> data = new HashMap<>();
        data.put("restaurantId", restaurantId);
        data.put("averageRating", avgRating);
        ApiResponse response = new ApiResponse(true, "Average rating fetched successfully", data);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delivery-person/{deliveryPersonId}/rating")
    public ResponseEntity<ApiResponse> getDeliveryPersonRating(@PathVariable Long deliveryPersonId) {
        Double avgRating = reviewService.getDeliveryPersonAverageRating(deliveryPersonId);
        Map<String, Object> data = new HashMap<>();
        data.put("deliveryPersonId", deliveryPersonId);
        data.put("averageRating", avgRating);
        ApiResponse response = new ApiResponse(true, "Average rating fetched successfully", data);
        return ResponseEntity.ok(response);
    }

    // Delete review (for inappropriate content)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        ApiResponse response = new ApiResponse(true, "Review deleted successfully");
        return ResponseEntity.ok(response);
    }
}