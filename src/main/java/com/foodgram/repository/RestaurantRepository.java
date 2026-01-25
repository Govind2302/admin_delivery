package com.foodgram.repository;

import com.foodgram.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByUserId(Long userId);

    Page<Restaurant> findByVerificationStatus(Restaurant.VerificationStatus status, Pageable pageable);

    Page<Restaurant> findByCuisineType(String cuisineType, Pageable pageable);

    Page<Restaurant> findByVerificationStatusAndCuisineType(
            Restaurant.VerificationStatus status,
            String cuisineType,
            Pageable pageable
    );

    List<Restaurant> findAllByVerificationStatus(Restaurant.VerificationStatus status);
}