package com.foodgram.dto.deliveryperson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DelLoginRequest {
    private String email;
    private String password;
}

