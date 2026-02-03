package com.foodgram.service;

import com.foodgram.dto.deliveryperson.DeliveryPersonDTO;
import com.foodgram.dto.deliveryperson.DeliveryPersonProfileDto;
import com.foodgram.dto.orders.DeliveryOrderDTO;
import com.foodgram.dto.orders.OrderDTO;
import com.foodgram.dto.orders.OrderItemDTO;
import com.foodgram.dto.response.DeliveryPersonResponse;
import com.foodgram.model.Deliveries;
import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.Orders;
import com.foodgram.model.User;
import com.foodgram.repository.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(DeliveryPersonService.class);

    public List<DeliveryOrderDTO> getOrdersForDeliveryPerson(int dpId) {
        log.info("Fetching deliveries for deliveryPersonId={}", dpId);

        List<Deliveries> deliveries = deliveriesRepository.findByDeliveryPerson_DeliveryPersonId(dpId);
        log.debug("Found {} deliveries", deliveries.size());

        return deliveries.stream().map(delivery -> {
            log.info("Mapping deliveryId={}", delivery.getDelId());

            Orders order = delivery.getOrders();
            log.debug("OrderId={} status={} total={}",
                    order.getOrderId(), order.getOrderStatus(), order.getTotalAmount());

            // Map order items
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream().map(item -> {
                log.debug("Mapping orderItemId={} menuItem={}",
                        item.getOrderItemId(), item.getMenuItem().getName());

                OrderItemDTO dto = new OrderItemDTO();
                dto.setItemId(Math.toIntExact(item.getMenuItem().getItemId()));
                dto.setName(item.getMenuItem().getName());
                dto.setPrice(item.getPrice());
                dto.setQuantity(item.getQuantity());
                return dto;
            }).collect(Collectors.toList());

            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(order.getOrderId());
            orderDTO.setOrderStatus(order.getOrderStatus().name());
            orderDTO.setTotalAmount(order.getTotalAmount());
            orderDTO.setOrderDate(order.getOrderDate().toString());
            orderDTO.setCustomerName(order.getUser().getFullName());
            orderDTO.setRestaurantName(order.getRestaurants().getName());
            orderDTO.setItems(itemDTOs);

            DeliveryOrderDTO dto = new DeliveryOrderDTO();
            dto.setDeliveryId(delivery.getDelId());
            dto.setDeliveryPersonId(delivery.getDeliveryPerson().getDeliveryPersonId());
            dto.setDeliveryStatus(delivery.getStatus().name());
            dto.setDeliveryTime(delivery.getDelTime() != null ? delivery.getDelTime().toString() : null);
            dto.setOrder(orderDTO);

            log.info("Finished mapping deliveryId={} with orderId={}",
                    delivery.getDelId(), order.getOrderId());

            return dto;
        }).collect(Collectors.toList());
    }



    public DeliveryPersonProfileDto getProfile(Long dpId) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(dpId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));
        return toDto(deliveryPerson);
    }

    @Transactional
    public Orders cancelOrder(int orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Only allow cancellation if order is not already delivered
        if (order.getOrderStatus() == Orders.OrderStatus.delivered) {
            throw new RuntimeException("Delivered orders cannot be cancelled");
        }

        order.setOrderStatus(Orders.OrderStatus.cancelled); // add CANCELLED enum in Orders.OrderStatus if not present
        return orderRepository.save(order);
    }


    public DeliveryPersonProfileDto updateProfile(Long dpId, DeliveryPersonProfileDto dto) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(dpId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        // Update fields
        deliveryPerson.setVehicleNumber(dto.getVehicleNumber());
        deliveryPerson.setOperatingArea(dto.getOperatingArea());
        deliveryPerson.setStatus(DeliveryPerson.VerificationStatus.valueOf(dto.getStatus()));
        deliveryPerson.setEarnings(dto.getEarnings());

        DeliveryPerson updated = deliveryPersonRepository.save(deliveryPerson);
        return toDto(updated);
    }

    private DeliveryPersonProfileDto toDto(DeliveryPerson deliveryPerson) {
        DeliveryPersonProfileDto dto = new DeliveryPersonProfileDto();
        dto.setDeliveryPersonId((long) deliveryPerson.getDeliveryPersonId());
        dto.setUserId(deliveryPerson.getUser().getUserId());
        dto.setFullName(deliveryPerson.getUser().getFullName());
        dto.setEmail(deliveryPerson.getUser().getEmail());
        dto.setPhone(deliveryPerson.getUser().getPhone());
        dto.setVehicleNumber(deliveryPerson.getVehicleNumber());
        dto.setOperatingArea(deliveryPerson.getOperatingArea());
        dto.setStatus(deliveryPerson.getStatus().name());
        dto.setEarnings(deliveryPerson.getEarnings());
        return dto;
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






