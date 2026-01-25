package com.foodgram.controller;

import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.ComplaintResponse;
import com.foodgram.dto.response.ReviewResponse;
import com.foodgram.service.ComplaintService;
import com.foodgram.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private ReviewService reviewService;

    // Get all feedback (combined complaints and reviews)
    @GetMapping
    public ResponseEntity<ApiResponse> getAllFeedback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type // "complaints" or "reviews"
    ) {
        Map<String, Object> feedbackData = new HashMap<>();

        if (type == null || type.equals("complaints")) {
            Page<ComplaintResponse> complaints = complaintService.getAllComplaints(page, size, null, null);
            feedbackData.put("complaints", complaints);
        }

        if (type == null || type.equals("reviews")) {
            Page<ReviewResponse> reviews = reviewService.getAllReviews(page, size, null, null, null);
            feedbackData.put("reviews", reviews);
        }

        ApiResponse response = new ApiResponse(true, "Feedback fetched successfully", feedbackData);
        return ResponseEntity.ok(response);
    }

    // Get feedback statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getFeedbackStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Get complaints count by status
        // Get average ratings
        // Get total reviews count
        // etc.

        stats.put("message", "Feedback statistics endpoint - implement as needed");

        ApiResponse response = new ApiResponse(true, "Statistics fetched successfully", stats);
        return ResponseEntity.ok(response);
    }
}