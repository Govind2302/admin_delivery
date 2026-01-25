package com.foodgram.repository;

import com.foodgram.model.Delivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(Long orderId);

    Page<Delivery> findByDeliveryPersonId(Long deliveryPersonId, Pageable pageable);

    List<Delivery> findAllByDeliveryPersonId(Long deliveryPersonId);

    Page<Delivery> findByStatus(Delivery.DeliveryStatus status, Pageable pageable);

    List<Delivery> findAllByStatus(Delivery.DeliveryStatus status);
}