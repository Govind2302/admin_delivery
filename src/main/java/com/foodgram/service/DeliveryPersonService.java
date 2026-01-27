package com.foodgram.service;

import com.foodgram.dto.deliveryperson.DeliveryPersonDTO;
import com.foodgram.dto.response.DeliveryPersonResponse;
import com.foodgram.model.Deliveries;
import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.Orders;
import com.foodgram.model.User;
import com.foodgram.repository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryPersonService {

    @Autowired
    private DeliveryPersonProfileRepository deliveryPersonProfileRepository;


    DeliveryPerson deliveryPerson;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository  orderRepository;

    @Autowired
    private DeliveriesRepository deliveriesRepository;

    @Autowired
    private final DeliveryPersonRepository deliveryPersonRepository;

    public DeliveryPersonService(DeliveryPersonRepository deliveryPersonRepository) {
        this.deliveryPersonRepository = deliveryPersonRepository;
    }


    @Transactional
    public DeliveryPersonResponse createDeliveryPerson(@Valid DeliveryPersonDTO request) {
        // Step 1: Fetch the user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Step 2: Validate role
        if (user.getRole() != User.Role.delivery_person) {
            throw new IllegalStateException("Only users with role 'delivery_person' can be linked here");
        }

        // Step 3: Create delivery person profile
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setUser(user);
        deliveryPerson.setVehicleNumber(request.getVehicleNumber());
        deliveryPerson.setOperatingArea(request.getOperatingArea());
        deliveryPerson.setStatus(DeliveryPerson.VerificationStatus.pending);
        deliveryPerson.setEarnings(request.getEarnings() );

        DeliveryPerson saved = deliveryPersonRepository.save(deliveryPerson);

        // Step 4: Map to response
        DeliveryPersonDTO dto = new DeliveryPersonDTO();
        dto.setUserId(saved.getUser().getUserId());
        dto.setVehicleNumber(saved.getVehicleNumber());
        dto.setOperatingArea(saved.getOperatingArea());
        dto.setStatus(saved.getStatus().name());
        dto.setEarnings(saved.getEarnings());

        return new DeliveryPersonResponse(
                (long) saved.getDeliveryPersonId(),
                dto,
                saved.getUser() != null ? saved.getUser().getFullName() : null,
                saved.getUser() != null ? saved.getUser().getEmail() : null,
                "Delivery person created successfully"
        );
    }

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


    public DeliveryPersonResponse updateDeliveryPerson(long id, @Valid DeliveryPersonDTO request) {

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with id: " + id));

        // Update fields
        deliveryPerson.setVehicleNumber(request.getVehicleNumber());
        deliveryPerson.setOperatingArea(request.getOperatingArea());

        try {
            DeliveryPerson.VerificationStatus statusEnum =
                    DeliveryPerson.VerificationStatus.valueOf(request.getStatus().toLowerCase());
            deliveryPerson.setStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + request.getStatus());
        }

        deliveryPerson.setEarnings(request.getEarnings());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
            deliveryPerson.setUser(user);
        }

        DeliveryPerson updated = deliveryPersonRepository.save(deliveryPerson);

        // Map entity to DTO
        DeliveryPersonDTO dto = new DeliveryPersonDTO();
        dto.setUserId(updated.getUser().getUserId());
        dto.setVehicleNumber(updated.getVehicleNumber());
        dto.setOperatingArea(updated.getOperatingArea());
        dto.setStatus(updated.getStatus().name());
        dto.setEarnings(updated.getEarnings());

        // Wrap in response with all fields populated
        return new DeliveryPersonResponse(
                (long) updated.getDeliveryPersonId(),
                dto,
                updated.getUser() != null ? updated.getUser().getFullName() : null,
                updated.getUser() != null ? updated.getUser().getEmail() : null,
                "Delivery person updated successfully"
        );
    }

    // 🔹 Get all delivery persons with optional filters
    public Page<DeliveryPersonResponse> getAllDeliveryPersons(int page, int size, String verificationStatus, String operatingArea) {
        Page<DeliveryPerson> deliveryPersons;

        if (verificationStatus != null && operatingArea != null) {
            deliveryPersons = deliveryPersonRepository.findByStatusAndOperatingArea(
                    DeliveryPerson.VerificationStatus.valueOf(verificationStatus.toLowerCase()), operatingArea, PageRequest.of(page, size));
        } else if (verificationStatus != null) {
            deliveryPersons = deliveryPersonRepository.findByStatus(
                    DeliveryPerson.VerificationStatus.valueOf(verificationStatus.toLowerCase()), PageRequest.of(page, size));
        } else if (operatingArea != null) {
            deliveryPersons = deliveryPersonRepository.findByOperatingArea(
                    operatingArea, PageRequest.of(page, size));
        } else {
            deliveryPersons = deliveryPersonRepository.findAll(PageRequest.of(page, size));
        }

        return deliveryPersons.map(this::mapToResponse);
    }

    // 🔹 Get by deliveryPersonId
    public DeliveryPersonResponse getDeliveryPersonById(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with id: " + id));
        return mapToResponse(deliveryPerson);
    }

    // 🔹 Get by userId
    public DeliveryPersonResponse getDeliveryPersonByUserId(Long userId) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found for userId: " + userId));
        return mapToResponse(deliveryPerson);
    }

    // 🔹 Delete delivery person
    public void deleteDeliveryPerson(Long id) {
        if (!deliveryPersonRepository.existsById(id)) {
            throw new RuntimeException("Delivery person not found with id: " + id);
        }
        deliveryPersonRepository.deleteById(id);
    }

    // 🔹 Update verification status
    public DeliveryPersonResponse updateVerificationStatus(Long id, String status) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found with id: " + id));

        try {
            deliveryPerson.setStatus(DeliveryPerson.VerificationStatus.valueOf(status.toLowerCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }

        DeliveryPerson updated = deliveryPersonRepository.save(deliveryPerson);
        return mapToResponse(updated);
    }

    // 🔹 Get all pending delivery persons
    public List<DeliveryPersonResponse> getPendingDeliveryPersons(int page, int size) {
        Page<DeliveryPerson> pendingPage =
                deliveryPersonRepository.findByStatus(
                        DeliveryPerson.VerificationStatus.pending,
                        PageRequest.of(page, size)
                );

        return pendingPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Helper: map entity → response
    private DeliveryPersonResponse mapToResponse(DeliveryPerson deliveryPerson) {
        DeliveryPersonDTO dto = new DeliveryPersonDTO();
        dto.setUserId(deliveryPerson.getUser().getUserId());
        dto.setVehicleNumber(deliveryPerson.getVehicleNumber());
        dto.setOperatingArea(deliveryPerson.getOperatingArea());
        dto.setStatus(deliveryPerson.getStatus().name());
        dto.setEarnings(deliveryPerson.getEarnings());

        return new DeliveryPersonResponse(
                (long) deliveryPerson.getDeliveryPersonId(),
                dto,
                deliveryPerson.getUser() != null ? deliveryPerson.getUser().getFullName() : null,
                deliveryPerson.getUser() != null ? deliveryPerson.getUser().getEmail() : null,
                "Success"
        );
    }

    public List<Deliveries> getOrdersForDeliveryPerson(int dpId) {
        return deliveriesRepository.findByDeliveryPerson_DeliveryPersonId(dpId);
    }





    public Orders markOrderDelivered(int orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Only allow delivery person to set "delivered"
        if (order.getOrderStatus() != Orders.OrderStatus.preparing
                && order.getOrderStatus() != Orders.OrderStatus.confirmed) {
            throw new RuntimeException("Order cannot be marked delivered at this stage");
        }

        order.setOrderStatus(Orders.OrderStatus.delivered);
        return orderRepository.save(order);
    }

    public Orders markOutForDelivery(int orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Only allow transition if restaurant has marked it ready
        if (order.getOrderStatus() != Orders.OrderStatus.preparing) {
            throw new RuntimeException("Order must be prepared before delivery");
        }

        order.setOrderStatus(Orders.OrderStatus.out_for_delivery); // add this enum
        return orderRepository.save(order);
    }
}






