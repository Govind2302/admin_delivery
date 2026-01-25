package com.foodgram.dto.response;

import com.foodgram.dto.deliveryperson.DeliveryPersonDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPersonResponse {
//
private Long deliveryPersonId;
    private DeliveryPersonDTO deliveryPerson;  // all delivery person details here
    private String userName;
    private String userEmail;
    private String message;



    public DeliveryPersonResponse(DeliveryPersonDTO deliveryPerson, String message) {
        this.deliveryPerson = deliveryPerson;
        this.message = message;
    }

}