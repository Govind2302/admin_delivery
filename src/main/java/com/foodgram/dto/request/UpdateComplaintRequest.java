package com.foodgram.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateComplaintRequest {

    private String status; // new, in_progress, resolved, closed

    private String response; // Admin's response to the complaint
}