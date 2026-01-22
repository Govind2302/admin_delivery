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
        DeliveryPerson deliveryPersonNew=deliveryPersonProfileRepository.findById(deliveryPerson.getDeliveryPersonId()).orElseThrow(()->new RuntimeException("No Delivery Person Found"));
        deliveryPersonNew.setVehicleNumber(deliveryPerson.getVehicleNumber());
        deliveryPersonNew.setStatus(deliveryPerson.getStatus());
        deliveryPersonNew.setEarnings(deliveryPerson.getEarnings());
        deliveryPersonNew.setOperatingArea(deliveryPerson.getOperatingArea());
        return deliveryPersonProfileRepository.save(deliveryPerson);
    }
    /*since DeliveryPerson has user as object we need to send entire user object
    from endpoint(frontend), instead we can just make a dto class for DeliveryPerson that will auto fetch user details
     */


}
