package com.foodgram.repository;

import com.foodgram.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get notifications for specific user
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    List<Notification> findAllByUserId(Long userId);

    // Get unread notifications for user
    Page<Notification> findByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable);

    List<Notification> findAllByUserIdAndIsRead(Long userId, Boolean isRead);

    // Get admin notifications (userId is null)
    Page<Notification> findByUserIdIsNull(Pageable pageable);

    List<Notification> findAllByUserIdIsNull();

    // Get unread admin notifications
    List<Notification> findAllByUserIdIsNullAndIsRead(Boolean isRead);

    // Get by type
    Page<Notification> findByType(Notification.NotificationType type, Pageable pageable);

    // Count unread notifications
    long countByUserIdAndIsRead(Long userId, Boolean isRead);

    long countByUserIdIsNullAndIsRead(Boolean isRead);
}