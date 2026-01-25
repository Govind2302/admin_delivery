package com.foodgram.service;

import com.foodgram.dto.request.UpdateRestaurantRequest;
import com.foodgram.dto.response.RestaurantResponse;
import com.foodgram.exception.BadRequestException;
import com.foodgram.exception.ResourceNotFoundException;
import com.foodgram.model.Restaurant;
import com.foodgram.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private RestaurantRepository restaurantRepository;

    // Get all restaurants with pagination and filters
    public Page<RestaurantResponse> getAllRestaurants(int page, int size, String verificationStatus, String cuisineType) {
        logger.info("Fetching restaurants - page: {}, size: {}, status: {}, cuisine: {}",
                page, size, verificationStatus, cuisineType);

        Pageable pageable = PageRequest.of(page, size, Sort.by("restaurantId").descending());
        Page<Restaurant> restaurants;

        if (verificationStatus != null && cuisineType != null) {
            restaurants = restaurantRepository.findByVerificationStatusAndCuisineType(
                    Restaurant.VerificationStatus.valueOf(verificationStatus),
                    cuisineType,
                    pageable
            );
        } else if (verificationStatus != null) {
            restaurants = restaurantRepository.findByVerificationStatus(
                    Restaurant.VerificationStatus.valueOf(verificationStatus),
                    pageable
            );
        } else if (cuisineType != null) {
            restaurants = restaurantRepository.findByCuisineType(cuisineType, pageable);
        } else {
            restaurants = restaurantRepository.findAll(pageable);
        }

        return restaurants.map(this::convertToResponse);
    }

    // Get restaurant by ID
    public RestaurantResponse getRestaurantById(Long restaurantId) {
        logger.info("Fetching restaurant by ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        return convertToResponse(restaurant);
    }

    // Update restaurant
    public RestaurantResponse updateRestaurant(Long restaurantId, UpdateRestaurantRequest request) {
        logger.info("Updating restaurant ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        // Update fields
        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getCuisineType() != null) {
            restaurant.setCuisineType(request.getCuisineType());
        }
        if (request.getContactNumber() != null) {
            restaurant.setContactNumber(request.getContactNumber());
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getOpenTime() != null) {
            try {
                restaurant.setOpenTime(LocalTime.parse(request.getOpenTime(), TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid open time format. Use HH:mm (e.g., 09:00)");
            }
        }
        if (request.getCloseTime() != null) {
            try {
                restaurant.setCloseTime(LocalTime.parse(request.getCloseTime(), TIME_FORMATTER));
            } catch (DateTimeParseException e) {
                throw new BadRequestException("Invalid close time format. Use HH:mm (e.g., 22:00)");
            }
        }
        if (request.getVerificationStatus() != null) {
            try {
                restaurant.setVerificationStatus(
                        Restaurant.VerificationStatus.valueOf(request.getVerificationStatus())
                );
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid verification status. Allowed: pending, verified, rejected");
            }
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        logger.info("Restaurant updated successfully: {}", updatedRestaurant.getName());

        return convertToResponse(updatedRestaurant);
    }

    // Delete restaurant
    public void deleteRestaurant(Long restaurantId) {
        logger.info("Deleting restaurant ID: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        restaurantRepository.delete(restaurant);
        logger.info("Restaurant deleted successfully: {}", restaurant.getName());
    }

    // Update verification status
    public RestaurantResponse updateVerificationStatus(Long restaurantId, String status) {
        logger.info("Updating verification status for restaurant ID: {} to {}", restaurantId, status);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        try {
            restaurant.setVerificationStatus(Restaurant.VerificationStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Allowed: pending, verified, rejected");
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        logger.info("Verification status updated successfully");

        return convertToResponse(updatedRestaurant);
    }

    // Update rating
    public RestaurantResponse updateRating(Long restaurantId, BigDecimal rating) {
        logger.info("Updating rating for restaurant ID: {} to {}", restaurantId, rating);

        if (rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5.0")) > 0) {
            throw new BadRequestException("Rating must be between 0.0 and 5.0");
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));

        restaurant.setRating(rating);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        logger.info("Rating updated successfully");

        return convertToResponse(updatedRestaurant);
    }

    // Get pending verification restaurants
    public List<RestaurantResponse> getPendingRestaurants() {
        logger.info("Fetching pending verification restaurants");

        List<Restaurant> restaurants = restaurantRepository.findAllByVerificationStatus(
                Restaurant.VerificationStatus.pending
        );

        return restaurants.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get restaurant by user ID
    public RestaurantResponse getRestaurantByUserId(Long userId) {
        logger.info("Fetching restaurant by user ID: {}", userId);

        Restaurant restaurant = restaurantRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found for user ID: " + userId));

        return convertToResponse(restaurant);
    }

    // Helper method
    private RestaurantResponse convertToResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getRestaurantId(),
                restaurant.getUserId(),
                restaurant.getName(),
                restaurant.getCuisineType(),
                restaurant.getContactNumber(),
                restaurant.getAddress(),
                restaurant.getOpenTime() != null ? restaurant.getOpenTime().format(TIME_FORMATTER) : null,
                restaurant.getCloseTime() != null ? restaurant.getCloseTime().format(TIME_FORMATTER) : null,
                restaurant.getVerificationStatus().name(),
                restaurant.getRating()
        );
    }
}