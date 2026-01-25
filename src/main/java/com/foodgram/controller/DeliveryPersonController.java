package com.foodgram.controller;


import com.foodgram.dto.deliveryperson.DelLoginRequest;
import com.foodgram.dto.deliveryperson.DelRegisterRequest;
import com.foodgram.dto.deliveryperson.DeliveryPersonDTO;
import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.User;
import com.foodgram.repository.DeliveryPersonProfileRepository;
import com.foodgram.repository.UserRepository;
import com.foodgram.service.AuthService;
import com.foodgram.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-person")
public class DeliveryPersonController {

    @Autowired
    private DeliveryPersonService deliveryPersonService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryPersonProfileRepository  deliveryPersonProfileRepository;


    @GetMapping("{dpId}/user/{userId}")
    public DeliveryPerson getProfile(@PathVariable int dpId,@PathVariable long userId){
        return deliveryPersonService.getProfileDetails(dpId,userId);
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


    //for delivery Auth
    @Autowired
    private AuthService authService;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody DelRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody DelLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/profile")
    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    public ResponseEntity<?> createProfile(@RequestBody DeliveryPersonDTO dto) {
        DeliveryPerson dp = new DeliveryPerson();
        dp.setVehicleNumber(dto.getVehicleNumber());
        dp.setOperatingArea(dto.getOperatingArea());
        dp.setEarnings(0.0);
        dp.setStatus(DeliveryPerson.VerificationStatus.pending);

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        dp.setUser(user);

        DeliveryPerson saved = deliveryPersonProfileRepository.save(dp);
        return ResponseEntity.ok(saved);
    }

}
