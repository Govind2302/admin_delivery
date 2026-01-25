package com.foodgram.service;

import com.foodgram.dto.request.UpdateDeliveryRequest;
import com.foodgram.dto.response.DeliveryResponse;
import com.foodgram.exception.BadRequestException;
import com.foodgram.exception.ResourceNotFoundException;
import com.foodgram.model.*;
import com.foodgram.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    // Get all deliveries with pagination and filters
    public Page<DeliveryResponse> getAllDeliveries(
            int page, int size, String status, Long deliveryPersonId) {

        logger.info("Fetching deliveries - page: {}, size: {}, status: {}, deliveryPersonId: {}",
                page, size, status, deliveryPersonId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("deliveryId").descending());
        Page<Delivery> deliveries;

        if (status != null && deliveryPersonId != null) {
            deliveries = deliveryRepository.findByStatus(
                    Delivery.DeliveryStatus.valueOf(status), pageable);
            // Filter by delivery person (post-query filtering)
            deliveries = deliveries.map(d -> {
                if (d.getDeliveryPersonId() != null && d.getDeliveryPersonId().equals(deliveryPersonId)) {
                    return d;
                }
                return null;
            });
        } else if (status != null) {
            deliveries = deliveryRepository.findByStatus(
                    Delivery.DeliveryStatus.valueOf(status), pageable);
        } else if (deliveryPersonId != null) {
            deliveries = deliveryRepository.findByDeliveryPersonId(deliveryPersonId, pageable);
        } else {
            deliveries = deliveryRepository.findAll(pageable);
        }

        return deliveries.map(this::convertToResponse);
    }

    // Get delivery by ID
    public DeliveryResponse getDeliveryById(Long deliveryId) {
        logger.info("Fetching delivery by ID: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        return convertToResponse(delivery);
    }

    // Get delivery by order ID
    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        logger.info("Fetching delivery by order ID: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order ID: " + orderId));

        return convertToResponse(delivery);
    }

    // Update delivery (reassign delivery person and/or update status)
    @Transactional
    public DeliveryResponse updateDelivery(Long deliveryId, UpdateDeliveryRequest request) {
        logger.info("Updating delivery ID: {}", deliveryId);

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with ID: " + deliveryId));

        // Update delivery person assignment
        if (request.getDeliveryPersonId() != null) {
            // Verify delivery person exists
            DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(request.getDeliveryPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Delivery person not found with ID: " + request.getDeliveryPersonId()));

            // Check if delivery person is verified
            if (deliveryPerson.getVerificationStatus() != DeliveryPerson.VerificationStatus.verified) {
                throw new BadRequestException("Delivery person is not verified");
            }

            delivery.setDeliveryPersonId(request.getDeliveryPersonId());
            logger.info("Delivery person updated to: {}", request.getDeliveryPersonId());

            // Update related order status
            Order order = orderRepository.findById(delivery.getOrderId()).orElse(null);
            if (order != null && order.getOrderStatus() != Order.OrderStatus.delivered) {
                order.setOrderStatus(Order.OrderStatus.out_for_delivery);
                orderRepository.save(order);
            }
        }

        // Update delivery status
        if (request.getStatus() != null) {
            try {
                Delivery.DeliveryStatus newStatus = Delivery.DeliveryStatus.valueOf(request.getStatus());
                delivery.setStatus(newStatus);
                logger.info("Delivery status updated to: {}", newStatus);

                // If delivered, set delivery time and update order status
                if (newStatus == Delivery.DeliveryStatus.delivered) {
                    delivery.setDeliveryTime(LocalDateTime.now());

                    // Update order status to delivered
                    Order order = orderRepository.findById(delivery.getOrderId()).orElse(null);
                    if (order != null) {
                        order.setOrderStatus(Order.OrderStatus.delivered);
                        orderRepository.save(order);
                        logger.info("Order status updated to delivered");
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(
                        "Invalid delivery status. Allowed: picked_up, in_transit, delivered, failed_attempt");
            }
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        logger.info("Delivery updated successfully");

        return convertToResponse(updatedDelivery);
    }

    // Update delivery status only
    public DeliveryResponse updateDeliveryStatus(Long deliveryId, String status) {
        logger.info("Updating status for delivery ID: {} to {}", deliveryId, status);

        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setStatus(status);

        return updateDelivery(deliveryId, request);
    }

    // Assign delivery person
    public DeliveryResponse assignDeliveryPerson(Long deliveryId, Long deliveryPersonId) {
        logger.info("Assigning delivery person {} to delivery {}", deliveryPersonId, deliveryId);

        UpdateDeliveryRequest request = new UpdateDeliveryRequest();
        request.setDeliveryPersonId(deliveryPersonId);

        return updateDelivery(deliveryId, request);
    }

    // Get deliveries by delivery person
    public List<DeliveryResponse> getDeliveriesByDeliveryPerson(Long deliveryPersonId) {
        logger.info("Fetching deliveries for delivery person ID: {}", deliveryPersonId);

        List<Delivery> deliveries = deliveryRepository.findAllByDeliveryPersonId(deliveryPersonId);

        return deliveries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get active deliveries (picked_up or in_transit)
    public List<DeliveryResponse> getActiveDeliveries() {
        logger.info("Fetching active deliveries");

        List<Delivery> pickedUp = deliveryRepository.findAllByStatus(Delivery.DeliveryStatus.picked_up);
        List<Delivery> inTransit = deliveryRepository.findAllByStatus(Delivery.DeliveryStatus.in_transit);

        pickedUp.addAll(inTransit);

        return pickedUp.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to convert Delivery to DeliveryResponse
    private DeliveryResponse convertToResponse(Delivery delivery) {
        // Fetch delivery person details
        DeliveryPerson deliveryPerson = null;
        if (delivery.getDeliveryPersonId() != null) {
            deliveryPerson = deliveryPersonRepository.findById(delivery.getDeliveryPersonId()).orElse(null);
        }

        User deliveryPersonUser = null;
        if (deliveryPerson != null) {
            deliveryPersonUser = userRepository.findById(deliveryPerson.getUserId()).orElse(null);
        }

        // Fetch order details
        Order order = orderRepository.findById(delivery.getOrderId()).orElse(null);

        String customerName = null;
        String restaurantName = null;
        String deliveryAddress = null;

        if (order != null) {
            // Get customer name
            User customer = userRepository.findById(order.getUserId()).orElse(null);
            if (customer != null) {
                customerName = customer.getFullName();
            }

            // Get restaurant name
            Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId()).orElse(null);
            if (restaurant != null) {
                restaurantName = restaurant.getName();
            }

            // Get delivery address
            UserAddresses address = userAddressRepository.findById(order.getAddressId()).orElse(null);
            if (address != null) {
                deliveryAddress = String.format("%s, %s, %s - %s",
                        address.getAddressLine(), address.getCity(), address.getState(), address.getPincode());
            }
        }

        return new DeliveryResponse(
                delivery.getDeliveryId(),
                delivery.getOrderId(),
                delivery.getDeliveryPersonId(),
                deliveryPersonUser != null ? deliveryPersonUser.getFullName() : null,
                deliveryPersonUser != null ? deliveryPersonUser.getPhone() : null,
                deliveryPerson != null ? deliveryPerson.getVehicleNumber() : null,
                delivery.getStatus().name(),
                delivery.getDeliveryTime(),
                customerName,
                restaurantName,
                deliveryAddress
        );
    }
}