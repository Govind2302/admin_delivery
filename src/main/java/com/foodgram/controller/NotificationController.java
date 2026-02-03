package com.foodgram.controller;

import com.foodgram.dto.response.ApiResponse;
import com.foodgram.dto.response.NotificationResponse;
import com.foodgram.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get notifications for current user (pass userId from authentication)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isRead
    ) {
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, page, size, isRead);
        ApiResponse response = new ApiResponse(true, "Notifications fetched successfully", notifications);
        return ResponseEntity.ok(response);
    }

    // Get unread count for user
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("unreadCount", count);
        ApiResponse response = new ApiResponse(true, "Unread count fetched successfully", data);
        return ResponseEntity.ok(response);
    }

    // Mark notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable Long id) {
        NotificationResponse notification = notificationService.markAsRead(id);
        ApiResponse response = new ApiResponse(true, "Notification marked as read", notification);
        return ResponseEntity.ok(response);
    }

    // Mark all as read for user
    @PutMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<ApiResponse> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsReadForUser(userId);
        ApiResponse response = new ApiResponse(true, "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        ApiResponse response = new ApiResponse(true, "Notification deleted successfully");
        return ResponseEntity.ok(response);
    }
}