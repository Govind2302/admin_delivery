package com.foodgram.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {

    private Long complaintId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long orderId;
    private String message;
    private String status;
    private String response;
    private LocalDateTime createdAt;
}