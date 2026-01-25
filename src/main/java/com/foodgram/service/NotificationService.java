package com.foodgram.service;

import com.foodgram.dto.response.NotificationResponse;
import com.foodgram.exception.ResourceNotFoundException;
import com.foodgram.model.Notification;
import com.foodgram.repository.NotificationRepository;
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
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    // Create notification
    public NotificationResponse createNotification(Long userId, String title, String message,
                                                   Notification.NotificationType type, Long referenceId) {

        logger.info("Creating notification for user: {}, type: {}", userId, type);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setIsRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification created successfully");

        return convertToResponse(savedNotification);
    }

    // Get all notifications for admin (userId is null)
    public Page<NotificationResponse> getAdminNotifications(int page, int size, Boolean isRead) {
        logger.info("Fetching admin notifications - page: {}, size: {}, isRead: {}", page, size, isRead);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications;

        if (isRead != null) {
            List<Notification> notificationList = notificationRepository.findAllByUserIdIsNullAndIsRead(isRead);
            notifications = (Page<Notification>) notificationList; // This needs proper pagination
        } else {
            notifications = notificationRepository.findByUserIdIsNull(pageable);
        }

        return notifications.map(this::convertToResponse);
    }

    // Get notifications for specific user
    public Page<NotificationResponse> getUserNotifications(Long userId, int page, int size, Boolean isRead) {
        logger.info("Fetching notifications for user: {} - page: {}, size: {}, isRead: {}",
                userId, page, size, isRead);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications;

        if (isRead != null) {
            notifications = notificationRepository.findByUserIdAndIsRead(userId, isRead, pageable);
        } else {
            notifications = notificationRepository.findByUserId(userId, pageable);
        }

        return notifications.map(this::convertToResponse);
    }

    // Mark notification as read
    public NotificationResponse markAsRead(Long notificationId) {
        logger.info("Marking notification as read: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        logger.info("Notification marked as read");

        return convertToResponse(updatedNotification);
    }

    // Mark all as read for user
    public void markAllAsReadForUser(Long userId) {
        logger.info("Marking all notifications as read for user: {}", userId);

        List<Notification> unreadNotifications = notificationRepository.findAllByUserIdAndIsRead(userId, false);

        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });

        logger.info("All notifications marked as read for user: {}", userId);
    }

    // Get unread count
    public long getUnreadCount(Long userId) {
        if (userId == null) {
            return notificationRepository.countByUserIdIsNullAndIsRead(false);
        }
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    // Delete notification
    public void deleteNotification(Long notificationId) {
        logger.info("Deleting notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        notificationRepository.delete(notification);
        logger.info("Notification deleted successfully");
    }

    // Helper method
    private NotificationResponse convertToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getNotificationId(),
                notification.getUserId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType().name(),
                notification.getIsRead(),
                notification.getReferenceId(),
                notification.getCreatedAt()
        );
    }
}