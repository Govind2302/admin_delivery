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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/delivery-person")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DeliveryPersonController {

    private static final Logger log = LoggerFactory.getLogger(DeliveryPersonController.class);



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
        System.out.println("LoginController received: email=" + request.getEmail() + ", password=" + request.getPassword());
        // or use a logger:
        log.info("LoginController received email={} password={}", request.getEmail(), request.getPassword());

        return ResponseEntity.ok(authService.login(request));
    }

    @DeleteMapping("/{dpId}")
    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    public ResponseEntity<?> deleteProfile(@PathVariable int dpId) {
        deliveryPersonService.deleteDeliveryPerson((long) dpId);
        return ResponseEntity.ok("Profile deleted successfully");
    }


    @GetMapping("/{dpId}/orders")
    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    public ResponseEntity<?> getAssignedOrders(@PathVariable int dpId) {
        return ResponseEntity.ok(deliveryPersonService.getOrdersForDeliveryPerson(dpId));
    }

    @PatchMapping("/orders/{orderId}/delivered")
    @PreAuthorize("hasRole('DELIVERY_PERSON')")
    public ResponseEntity<?> markDelivered(@PathVariable int orderId) {
        return ResponseEntity.ok(deliveryPersonService.markOrderDelivered(orderId));
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

        // 🔹 Role validation
        if (user.getRole() != User.Role.delivery_person) {
            throw new IllegalStateException("Only users with role 'delivery_person' can create a delivery profile");
        }

        dp.setUser(user);

        DeliveryPerson saved = deliveryPersonProfileRepository.save(dp);
        return ResponseEntity.ok(saved);
    }

}
