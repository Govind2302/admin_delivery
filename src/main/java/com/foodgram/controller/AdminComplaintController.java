package com.foodgram.controller;

import com.foodgram.dto.request.UpdateComplaintRequest;
import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.ComplaintResponse;
import com.foodgram.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/complaints")
public class AdminComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // Get all complaints with pagination and filters
    @GetMapping
    public ResponseEntity<ApiResponse> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId
    ) {
        Page<ComplaintResponse> complaints = complaintService.getAllComplaints(page, size, status, userId);
        ApiResponse response = new ApiResponse(true, "Complaints fetched successfully", complaints);
        return ResponseEntity.ok(response);
    }

    // Get complaint by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getComplaintById(@PathVariable Long id) {
        ComplaintResponse complaint = complaintService.getComplaintById(id);
        ApiResponse response = new ApiResponse(true, "Complaint fetched successfully", complaint);
        return ResponseEntity.ok(response);
    }

    // Update complaint (status and/or response)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody UpdateComplaintRequest request
    ) {
        ComplaintResponse updatedComplaint = complaintService.updateComplaint(id, request);
        ApiResponse response = new ApiResponse(true, "Complaint updated successfully", updatedComplaint);
        return ResponseEntity.ok(response);
    }

    // Update complaint status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        ComplaintResponse updatedComplaint = complaintService.updateComplaintStatus(id, status);
        ApiResponse response = new ApiResponse(true, "Complaint status updated successfully", updatedComplaint);
        return ResponseEntity.ok(response);
    }

    // Add response to complaint
    @PutMapping("/{id}/response")
    public ResponseEntity<ApiResponse> addResponse(
            @PathVariable Long id,
            @RequestParam String response
    ) {
        ComplaintResponse updatedComplaint = complaintService.addResponse(id, response);
        ApiResponse apiResponse = new ApiResponse(true, "Response added successfully", updatedComplaint);
        return ResponseEntity.ok(apiResponse);
    }

    // Get new complaints
    @GetMapping("/new")
    public ResponseEntity<ApiResponse> getNewComplaints() {
        List<ComplaintResponse> complaints = complaintService.getNewComplaints();
        ApiResponse response = new ApiResponse(true, "New complaints fetched successfully", complaints);
        return ResponseEntity.ok(response);
    }

    // Delete complaint
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        ApiResponse response = new ApiResponse(true, "Complaint deleted successfully");
        return ResponseEntity.ok(response);
    }
}