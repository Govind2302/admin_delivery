package com.foodgram.service;

import com.foodgram.dto.request.UpdateDeliveryPersonRequest;
import com.foodgram.dto.response.DeliveryPersonResponse;
import com.foodgram.exception.BadRequestException;
import com.foodgram.exception.ResourceNotFoundException;
import com.foodgram.model.DeliveryPerson;
import com.foodgram.model.User;
import com.foodgram.repository.DeliveryPersonRepository;
import com.foodgram.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryPersonService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryPersonService.class);

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all delivery persons with pagination and filters
    public Page<DeliveryPersonResponse> getAllDeliveryPersons(
            int page, int size, String verificationStatus, String operatingArea) {

        logger.info("Fetching delivery persons - page: {}, size: {}, status: {}, area: {}",
                page, size, verificationStatus, operatingArea);

        Pageable pageable = PageRequest.of(page, size, Sort.by("deliveryPersonId").descending());
        Page<DeliveryPerson> deliveryPersons;

        if (verificationStatus != null && operatingArea != null) {
            deliveryPersons = deliveryPersonRepository.findByVerificationStatusAndOperatingArea(
                    DeliveryPerson.VerificationStatus.valueOf(verificationStatus),
                    operatingArea,
                    pageable
            );
        } else if (verificationStatus != null) {
            deliveryPersons = deliveryPersonRepository.findByVerificationStatus(
                    DeliveryPerson.VerificationStatus.valueOf(verificationStatus),
                    pageable
            );
        } else if (operatingArea != null) {
            deliveryPersons = deliveryPersonRepository.findByOperatingArea(operatingArea, pageable);
        } else {
            deliveryPersons = deliveryPersonRepository.findAll(pageable);
        }

        return deliveryPersons.map(this::convertToResponse);
    }

    // Get delivery person by ID
    public DeliveryPersonResponse getDeliveryPersonById(Long deliveryPersonId) {
        logger.info("Fetching delivery person by ID: {}", deliveryPersonId);

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery person not found with ID: " + deliveryPersonId));

        return convertToResponse(deliveryPerson);
    }

    // Update delivery person
    public DeliveryPersonResponse updateDeliveryPerson(
            Long deliveryPersonId, UpdateDeliveryPersonRequest request) {

        logger.info("Updating delivery person ID: {}", deliveryPersonId);

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery person not found with ID: " + deliveryPersonId));

        // Update fields
        if (request.getVehicleNumber() != null) {
            deliveryPerson.setVehicleNumber(request.getVehicleNumber());
        }
        if (request.getOperatingArea() != null) {
            deliveryPerson.setOperatingArea(request.getOperatingArea());
        }
        if (request.getVerificationStatus() != null) {
            try {
                deliveryPerson.setVerificationStatus(
                        DeliveryPerson.VerificationStatus.valueOf(request.getVerificationStatus())
                );
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(
                        "Invalid verification status. Allowed: pending, verified, rejected");
            }
        }

        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        logger.info("Delivery person updated successfully: ID {}", updatedDeliveryPerson.getDeliveryPersonId());

        return convertToResponse(updatedDeliveryPerson);
    }

    // Delete delivery person
    public void deleteDeliveryPerson(Long deliveryPersonId) {
        logger.info("Deleting delivery person ID: {}", deliveryPersonId);

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery person not found with ID: " + deliveryPersonId));

        deliveryPersonRepository.delete(deliveryPerson);
        logger.info("Delivery person deleted successfully: ID {}", deliveryPersonId);
    }

    // Update verification status
    public DeliveryPersonResponse updateVerificationStatus(Long deliveryPersonId, String status) {
        logger.info("Updating verification status for delivery person ID: {} to {}",
                deliveryPersonId, status);

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery person not found with ID: " + deliveryPersonId));

        try {
            deliveryPerson.setVerificationStatus(DeliveryPerson.VerificationStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status. Allowed: pending, verified, rejected");
        }

        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        logger.info("Verification status updated successfully");

        return convertToResponse(updatedDeliveryPerson);
    }

    // Get pending verification delivery persons
    public List<DeliveryPersonResponse> getPendingDeliveryPersons() {
        logger.info("Fetching pending verification delivery persons");

        List<DeliveryPerson> deliveryPersons = deliveryPersonRepository.findAllByVerificationStatus(
                DeliveryPerson.VerificationStatus.pending
        );

        return deliveryPersons.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get delivery person by user ID
    public DeliveryPersonResponse getDeliveryPersonByUserId(Long userId) {
        logger.info("Fetching delivery person by user ID: {}", userId);

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery person not found for user ID: " + userId));

        return convertToResponse(deliveryPerson);
    }

    // Get profile details (for your existing controller)
    public DeliveryPerson getProfileDetails(int dpId, long userId) {
        logger.info("Fetching profile for delivery person ID: {} and user ID: {}", dpId, userId);

        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById((long) dpId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery person not found with ID: " + dpId));

        // Fetch and set user
        User user = userRepository.findById(userId).orElse(null);
        deliveryPerson.setUser(user);

        return deliveryPerson;
    }

    // Update profile details (for your existing controller)
    public DeliveryPerson updateProfileDetails(DeliveryPerson deliveryPerson) {
        logger.info("Updating profile for delivery person ID: {}", deliveryPerson.getDeliveryPersonId());
        return deliveryPersonRepository.save(deliveryPerson);
    }

    // Helper method
    private DeliveryPersonResponse convertToResponse(DeliveryPerson deliveryPerson) {
        // Fetch user details
        User user = userRepository.findById(deliveryPerson.getUserId()).orElse(null);

        return new DeliveryPersonResponse(
                deliveryPerson.getDeliveryPersonId(),
                deliveryPerson.getUserId(),
                user != null ? user.getFullName() : null,
                user != null ? user.getEmail() : null,
                deliveryPerson.getVehicleNumber(),
                deliveryPerson.getOperatingArea(),
                deliveryPerson.getVerificationStatus().name(),
                deliveryPerson.getEarnings()
        );
    }
}