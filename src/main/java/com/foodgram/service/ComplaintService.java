package com.foodgram.service;

import com.foodgram.dto.request.UpdateComplaintRequest;
import com.foodgram.dto.response.ComplaintResponse;
import com.foodgram.exception.BadRequestException;
import com.foodgram.exception.ResourceNotFoundException;
import com.foodgram.model.Complaint;
import com.foodgram.model.User;
import com.foodgram.repository.ComplaintRepository;
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
public class ComplaintService {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintService.class);

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all complaints with pagination and filters
    public Page<ComplaintResponse> getAllComplaints(int page, int size, String status, Long userId) {
        logger.info("Fetching complaints - page: {}, size: {}, status: {}, userId: {}",
                page, size, status, userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints;

        if (status != null && userId != null) {
            complaints = complaintRepository.findByStatus(
                    Complaint.ComplaintStatus.valueOf(status), pageable);
        } else if (status != null) {
            complaints = complaintRepository.findByStatus(
                    Complaint.ComplaintStatus.valueOf(status), pageable);
        } else if (userId != null) {
            complaints = complaintRepository.findByUserId(userId, pageable);
        } else {
            complaints = complaintRepository.findAll(pageable);
        }

        return complaints.map(this::convertToResponse);
    }

    // Get complaint by ID
    public ComplaintResponse getComplaintById(Long complaintId) {
        logger.info("Fetching complaint by ID: {}", complaintId);

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));

        return convertToResponse(complaint);
    }

    // Update complaint (status and/or response)
    public ComplaintResponse updateComplaint(Long complaintId, UpdateComplaintRequest request) {
        logger.info("Updating complaint ID: {}", complaintId);

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));

        // Update status
        if (request.getStatus() != null) {
            try {
                Complaint.ComplaintStatus newStatus = Complaint.ComplaintStatus.valueOf(request.getStatus());
                complaint.setStatus(newStatus);
                logger.info("Complaint status updated to: {}", newStatus);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(
                        "Invalid complaint status. Allowed: new_complaint, in_progress, resolved, closed");
            }
        }

        // Update response
        if (request.getResponse() != null) {
            complaint.setResponse(request.getResponse());
            logger.info("Admin response added to complaint");
        }

        Complaint updatedComplaint = complaintRepository.save(complaint);
        logger.info("Complaint updated successfully");

        return convertToResponse(updatedComplaint);
    }

    // Update complaint status only
    public ComplaintResponse updateComplaintStatus(Long complaintId, String status) {
        logger.info("Updating status for complaint ID: {} to {}", complaintId, status);

        UpdateComplaintRequest request = new UpdateComplaintRequest();
        request.setStatus(status);

        return updateComplaint(complaintId, request);
    }

    // Add response to complaint
    public ComplaintResponse addResponse(Long complaintId, String response) {
        logger.info("Adding response to complaint ID: {}", complaintId);

        UpdateComplaintRequest request = new UpdateComplaintRequest();
        request.setResponse(response);
        request.setStatus("in_progress"); // Auto-update status when responding

        return updateComplaint(complaintId, request);
    }

    // Get new complaints
    public List<ComplaintResponse> getNewComplaints() {
        logger.info("Fetching new complaints");

        List<Complaint> complaints = complaintRepository.findAllByStatus(
                Complaint.ComplaintStatus.new_complaint);

        return complaints.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Delete complaint
    public void deleteComplaint(Long complaintId) {
        logger.info("Deleting complaint ID: {}", complaintId);

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + complaintId));

        complaintRepository.delete(complaint);
        logger.info("Complaint deleted successfully");
    }

    // Helper method
    private ComplaintResponse convertToResponse(Complaint complaint) {
        User user = userRepository.findById(complaint.getUserId()).orElse(null);

        return new ComplaintResponse(
                complaint.getComplaintId(),
                complaint.getUserId(),
                user != null ? user.getFullName() : null,
                user != null ? user.getEmail() : null,
                complaint.getOrderId(),
                complaint.getMessage(),
                complaint.getStatus().getValue(),
                complaint.getResponse(),
                complaint.getCreatedAt()
        );
    }
}