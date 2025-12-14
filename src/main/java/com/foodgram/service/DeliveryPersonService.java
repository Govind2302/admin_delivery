package com.foodgram.service;

import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.User;
import com.foodgram.repository.DeliveryPersonProfileRepository;
import com.foodgram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryPersonService {

    @Autowired
    private DeliveryPersonProfileRepository deliveryPersonProfileRepository;


    DeliveryPerson deliveryPerson;
    @Autowired
    private UserRepository userRepository;

    public DeliveryPerson getProfileDetails(int dpId, long userId) {
        return deliveryPersonProfileRepository
                .findByDeliveryPersonIdAndUser_UserId(dpId, userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public DeliveryPerson updateProfileDetails(DeliveryPerson deliveryPerson) {
        User user = deliveryPerson.getUser();
        if (user != null) {
            userRepository.save(user);
        }
        return deliveryPersonProfileRepository.save(deliveryPerson);
    }


}
