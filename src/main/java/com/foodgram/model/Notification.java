package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "user_id")
    private Long userId; // null for admin notifications

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "reference_id") // Order ID, Complaint ID, etc.
    private Long referenceId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum NotificationType {
        ORDER_PLACED,
        ORDER_CONFIRMED,
        ORDER_DELIVERED,
        DELIVERY_ASSIGNED,
        COMPLAINT_RECEIVED,
        NEW_REVIEW,
        NEW_REGISTRATION,
        SYSTEM_ALERT,
        PAYMENT_RECEIVED
    }
}