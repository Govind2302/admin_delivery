package com.foodgram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long notificationId;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private Boolean isRead;
    private Long referenceId;
    private LocalDateTime createdAt;
}