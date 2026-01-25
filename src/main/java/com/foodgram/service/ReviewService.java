package com.foodgram.service;

import com.foodgram.dto.response.ReviewResponse;
import com.foodgram.exception.ResourceNotFoundException;
import com.foodgram.model.*;
import com.foodgram.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    // Get all reviews with pagination and filters
    public Page<ReviewResponse> getAllReviews(
            int page, int size, Long restaurantId, Long userId, Long deliveryPersonId) {

        logger.info("Fetching reviews - page: {}, size: {}, restaurantId: {}, userId: {}, deliveryPersonId: {}",
                page, size, restaurantId, userId, deliveryPersonId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews;

        if (restaurantId != null) {
            reviews = reviewRepository.findByRestaurantId(restaurantId, pageable);
        } else if (userId != null) {
            reviews = reviewRepository.findByUserId(userId, pageable);
        } else if (deliveryPersonId != null) {
            reviews = reviewRepository.findByDeliveryPersonId(deliveryPersonId, pageable);
        } else {
            reviews = reviewRepository.findAll(pageable);
        }

        return reviews.map(this::convertToResponse);
    }

    // Get review by ID
    public ReviewResponse getReviewById(Long reviewId) {
        logger.info("Fetching review by ID: {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        return convertToResponse(review);
    }

    // Get reviews by restaurant
    public List<ReviewResponse> getReviewsByRestaurant(Long restaurantId) {
        logger.info("Fetching reviews for restaurant ID: {}", restaurantId);

        List<Review> reviews = reviewRepository.findAllByRestaurantId(restaurantId);

        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get reviews by delivery person
    public List<ReviewResponse> getReviewsByDeliveryPerson(Long deliveryPersonId) {
        logger.info("Fetching reviews for delivery person ID: {}", deliveryPersonId);

        List<Review> reviews = reviewRepository.findAllByDeliveryPersonId(deliveryPersonId);

        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Delete review (for inappropriate content)
    public void deleteReview(Long reviewId) {
        logger.info("Deleting review ID: {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        reviewRepository.delete(review);
        logger.info("Review deleted successfully");
    }

    // Get average rating for restaurant
    public Double getRestaurantAverageRating(Long restaurantId) {
        logger.info("Calculating average rating for restaurant ID: {}", restaurantId);

        Double avgRating = reviewRepository.getAverageRatingByRestaurant(restaurantId);
        return avgRating != null ? avgRating : 0.0;
    }

    // Get average rating for delivery person
    public Double getDeliveryPersonAverageRating(Long deliveryPersonId) {
        logger.info("Calculating average rating for delivery person ID: {}", deliveryPersonId);

        Double avgRating = reviewRepository.getAverageRatingByDeliveryPerson(deliveryPersonId);
        return avgRating != null ? avgRating : 0.0;
    }

    // Helper method
    private ReviewResponse convertToResponse(Review review) {
        User user = userRepository.findById(review.getUserId()).orElse(null);

        Restaurant restaurant = null;
        if (review.getRestaurantId() != null) {
            restaurant = restaurantRepository.findById(review.getRestaurantId()).orElse(null);
        }

        DeliveryPerson deliveryPerson = null;
        User deliveryPersonUser = null;
        if (review.getDeliveryPersonId() != null) {
            deliveryPerson = deliveryPersonRepository.findById(review.getDeliveryPersonId()).orElse(null);
            if (deliveryPerson != null) {
                deliveryPersonUser = userRepository.findById(deliveryPerson.getUserId()).orElse(null);
            }
        }

        // Determine review type
        String reviewType = "unknown";
        if (review.getRestaurantId() != null) {
            reviewType = "restaurant";
        } else if (review.getItemId() != null) {
            reviewType = "item";
        } else if (review.getDeliveryPersonId() != null) {
            reviewType = "delivery";
        }

        return new ReviewResponse(
                review.getReviewId(),
                review.getUserId(),
                user != null ? user.getFullName() : null,
                user != null ? user.getEmail() : null,
                review.getRestaurantId(),
                restaurant != null ? restaurant.getName() : null,
                review.getItemId(),
                null, // Item name - need MenuItem repository to fetch
                review.getDeliveryPersonId(),
                deliveryPersonUser != null ? deliveryPersonUser.getFullName() : null,
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                reviewType
        );
    }
}