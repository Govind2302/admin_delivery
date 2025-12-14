package com.foodgram.repository;

import com.foodgram.model.Deliveries;
import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryPersonProfileRepository extends JpaRepository<DeliveryPerson,Integer> {

    Optional<DeliveryPerson> findByDeliveryPersonIdAndUser_UserId(int deliveryPersonId, long userId);



}
