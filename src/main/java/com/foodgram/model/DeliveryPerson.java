package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "delivery_person")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_person_id")
    private int deliveryPersonId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    @Column(name = "operating_area")
    private String operatingArea;
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus status;
    @Column(name ="earnings")
    private double earnings;


    public enum VerificationStatus {
        pending,
        verified,
        rejected
    }

    public Long getUserId() {
        return user != null ? user.getUserId() : null;
    }





}
