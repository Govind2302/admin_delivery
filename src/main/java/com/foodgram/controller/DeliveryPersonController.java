package com.foodgram.controller;


import com.foodgram.model.DeliveryPerson;
import com.foodgram.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-person")
public class DeliveryPersonController {

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @GetMapping("{dpId}/user/{userId}")
    public DeliveryPerson getProfile(@PathVariable int dpId,@PathVariable long userId){
        return deliveryPersonService.getProfileDetails(dpId, userId);
    }

    @PutMapping("/{dpId}")
    public DeliveryPerson updateProfile(@PathVariable int dpId, @RequestBody DeliveryPerson deliveryPerson) {
        deliveryPerson.setDeliveryPersonId(dpId);
        return deliveryPersonService.updateProfileDetails(deliveryPerson);
    }


}
