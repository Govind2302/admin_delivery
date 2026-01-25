package com.foodgram.dto.deliveryperson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
    public class DelRegisterRequest {
        private String fullName;
        private String email;
        private String password;
        private String phone;
        private String role;
    }

