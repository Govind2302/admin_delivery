package com.foodgram.repository;

import com.foodgram.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find by status
    Page<Order> findByOrderStatus(Order.OrderStatus status, Pageable pageable);

    // Find by user
    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findAllByUserId(Long userId);

    // Find by restaurant
    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);
    List<Order> findAllByRestaurantId(Long restaurantId);

    // Find by status and restaurant
    Page<Order> findByOrderStatusAndRestaurantId(
            Order.OrderStatus status,
            Long restaurantId,
            Pageable pageable
    );

    // Find by status and user
    Page<Order> findByOrderStatusAndUserId(
            Order.OrderStatus status,
            Long userId,
            Pageable pageable
    );

    // Find by date range
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    // Count by status
    long countByOrderStatus(Order.OrderStatus status);
}