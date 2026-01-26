package com.foodgram.repository;


import com.foodgram.model.Orders;
import com.foodgram.model.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {

    // 🔹 Find all orders assigned to a specific delivery person
    List<Orders> findByDeliveryPerson(DeliveryPerson deliveryPerson);

    // 🔹 Find all orders by delivery person ID
    List<Orders> findByDeliveryPerson_DeliveryPersonId(int deliveryPersonId);

    // 🔹 Find orders by status
    List<Orders> findByOrderStatus(Orders.OrderStatus status);

    // 🔹 Find orders by delivery person and status
    List<Orders> findByDeliveryPerson_DeliveryPersonIdAndOrderStatus(int deliveryPersonId, Orders.OrderStatus status);

    // 🔹 Find orders by user ID
    List<Orders> findByUser_UserId(Long userId);

    // 🔹 Find orders by restaurant ID
    List<Orders> findByRestaurants_RestaurantId(Long restaurantId);
}