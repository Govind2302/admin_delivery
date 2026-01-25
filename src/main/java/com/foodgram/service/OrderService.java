package com.foodgram.service;

import com.foodgram.dto.request.UpdateOrderRequest;
import com.foodgram.dto.response.OrderResponse;
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

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;  // Fixed name

    // Get all orders with pagination and filters
    public Page<OrderResponse> getAllOrders(
            int page, int size, String orderStatus, Long userId, Long restaurantId) {

        logger.info("Fetching orders - page: {}, size: {}, status: {}, userId: {}, restaurantId: {}",
                page, size, orderStatus, userId, restaurantId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orders;

        if (orderStatus != null && userId != null) {
            orders = orderRepository.findByOrderStatusAndUserId(
                    Order.OrderStatus.valueOf(orderStatus), userId, pageable);
        } else if (orderStatus != null && restaurantId != null) {
            orders = orderRepository.findByOrderStatusAndRestaurantId(
                    Order.OrderStatus.valueOf(orderStatus), restaurantId, pageable);
        } else if (orderStatus != null) {
            orders = orderRepository.findByOrderStatus(
                    Order.OrderStatus.valueOf(orderStatus), pageable);
        } else if (userId != null) {
            orders = orderRepository.findByUserId(userId, pageable);
        } else if (restaurantId != null) {
            orders = orderRepository.findByRestaurantId(restaurantId, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(this::convertToResponse);
    }

    // Get order by ID
    public OrderResponse getOrderById(Long orderId) {
        logger.info("Fetching order by ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        return convertToResponse(order);
    }

    // Update order (change status and/or assign delivery person)
    @Transactional
    public OrderResponse updateOrder(Long orderId, UpdateOrderRequest request) {
        logger.info("Updating order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // Update order status
        if (request.getOrderStatus() != null) {
            try {
                Order.OrderStatus newStatus = Order.OrderStatus.valueOf(request.getOrderStatus());
                order.setOrderStatus(newStatus);
                logger.info("Order status updated to: {}", newStatus);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(
                        "Invalid order status. Allowed: pending, confirmed, preparing, out_for_delivery, delivered, cancelled");
            }
        }

        // Assign delivery person
        if (request.getDeliveryPersonId() != null) {
            // Verify delivery person exists
            DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(request.getDeliveryPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Delivery person not found with ID: " + request.getDeliveryPersonId()));

            // Check if delivery person is verified
            if (deliveryPerson.getVerificationStatus() != DeliveryPerson.VerificationStatus.verified) {
                throw new BadRequestException("Delivery person is not verified");
            }

            // Check if delivery entry exists for this order
            Delivery delivery = deliveryRepository.findByOrderId(orderId).orElse(null);

            if (delivery == null) {
                // Create new delivery entry
                delivery = new Delivery();
                delivery.setOrderId(orderId);
                delivery.setDeliveryPersonId(request.getDeliveryPersonId());
                delivery.setStatus(Delivery.DeliveryStatus.picked_up);
                deliveryRepository.save(delivery);
                logger.info("Delivery entry created and assigned to delivery person: {}",
                        request.getDeliveryPersonId());
            } else {
                // Update existing delivery entry
                delivery.setDeliveryPersonId(request.getDeliveryPersonId());
                deliveryRepository.save(delivery);
                logger.info("Delivery person updated to: {}", request.getDeliveryPersonId());
            }

            // Update order status to out_for_delivery if it's not already
            if (order.getOrderStatus() == Order.OrderStatus.confirmed ||
                    order.getOrderStatus() == Order.OrderStatus.preparing) {
                order.setOrderStatus(Order.OrderStatus.out_for_delivery);
            }
        }

        Order updatedOrder = orderRepository.save(order);
        logger.info("Order updated successfully");

        return convertToResponse(updatedOrder);
    }

    // Update order status
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        logger.info("Updating status for order ID: {} to {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        try {
            order.setOrderStatus(Order.OrderStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid status. Allowed: pending, confirmed, preparing, out_for_delivery, delivered, cancelled");
        }

        Order updatedOrder = orderRepository.save(order);
        logger.info("Order status updated successfully");

        return convertToResponse(updatedOrder);
    }

    // Helper method to convert Order to OrderResponse
    private OrderResponse convertToResponse(Order order) {
        // Fetch related data
        User user = userRepository.findById(order.getUserId()).orElse(null);
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId()).orElse(null);
        UserAddresses address = userAddressRepository.findById(order.getAddressId()).orElse(null);

        String deliveryAddress = null;
        if (address != null) {
            deliveryAddress = String.format("%s, %s, %s - %s",
                    address.getAddressLine(), address.getCity(), address.getState(), address.getPincode());
        }

        return new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                user != null ? user.getFullName() : null,
                user != null ? user.getEmail() : null,
                order.getRestaurantId(),
                restaurant != null ? restaurant.getName() : null,
                order.getAddressId(),
                deliveryAddress,
                order.getPaymentId(),
                order.getOrderStatus().name(),
                order.getTotalAmount(),
                order.getOrderDate()
        );
    }
}