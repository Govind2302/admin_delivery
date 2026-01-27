package com.foodgram.repository;

import com.foodgram.model.Deliveries;
import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveriesRepository extends JpaRepository<Deliveries, Integer> {

    // Find deliveries by delivery person
    List<Deliveries> findByDeliveryPerson(DeliveryPerson deliveryPerson);

    // Find deliveries by delivery person ID
    List<Deliveries> findByDeliveryPerson_DeliveryPersonId(int deliveryPersonId);

    // Find deliveries by delivery person and status (through Orders)
    List<Deliveries> findByDeliveryPerson_DeliveryPersonIdAndOrders_OrderStatus(
            int deliveryPersonId, Orders.OrderStatus status);
}