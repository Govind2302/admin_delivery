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
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get admin notifications (system alerts)
    @GetMapping
    public ResponseEntity<ApiResponse> getAdminNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isRead
    ) {
        Page<NotificationResponse> notifications = notificationService.getAdminNotifications(page, size, isRead);
        ApiResponse response = new ApiResponse(true, "Admin notifications fetched successfully", notifications);
        return ResponseEntity.ok(response);
    }

    // Get unread count
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse> getUnreadCount() {
        long count = notificationService.getUnreadCount(null);
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

    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        ApiResponse response = new ApiResponse(true, "Notification deleted successfully");
        return ResponseEntity.ok(response);
    }
}