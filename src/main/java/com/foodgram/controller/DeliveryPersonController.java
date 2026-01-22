package com.foodgram.controller;


import com.foodgram.dto.deliveryperson.DeliveryPersonDTO;
import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.User;
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
    public DeliveryPerson updateProfile(@PathVariable int dpId, @RequestBody DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson deliveryPerson=new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(dpId);
        deliveryPerson.setEarnings(deliveryPersonDTO.getEarnings());
        deliveryPerson.setVehicleNumber(deliveryPersonDTO.getVehicleNumber());
        deliveryPerson.setOperatingArea(deliveryPersonDTO.getOperatingArea());
        deliveryPerson.setStatus(DeliveryPerson.VerificationStatus.valueOf(deliveryPersonDTO.getStatus()));
        User user=new User();
        user.setUserId(deliveryPersonDTO.getUserId());
        deliveryPerson.setUser(user);

        return deliveryPersonService.updateProfileDetails(deliveryPerson);
    }
    /*since DeliveryPerson has user as object we need to send entire user object
    from endpoint(frontend), instead we can just make a dto class for DeliveryPerson that will auto fetch user details
     */



}
