package com.foodgram.repository;

import com.foodgram.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    List<Review> findAllByRestaurantId(Long restaurantId);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    List<Review> findAllByUserId(Long userId);

    Page<Review> findByItemId(Long itemId, Pageable pageable);

    Page<Review> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);

    List<Review> findAllByDeliveryPersonId(Long deliveryPersonId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurantId = ?1")
    Double getAverageRatingByRestaurant(Long restaurantId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.deliveryPersonId = ?1")
    Double getAverageRatingByDeliveryPerson(Long deliveryPersonId);
}