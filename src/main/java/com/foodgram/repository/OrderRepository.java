package com.foodgram.repository;


import com.foodgram.model.Orders;
import com.foodgram.model.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {



    // 🔹 Find orders by status
    List<Orders> findByOrderStatus(Orders.OrderStatus status);


    // 🔹 Find orders by user ID
    List<Orders> findByUser_UserId(Long userId);

    // 🔹 Find orders by restaurant ID
    List<Orders> findByRestaurants_RestaurantId(Long restaurantId);
}