package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private int restaurantId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    private String name;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "contact_number")
    private String contactNumber;

    private String address;

    @Column(name = "open_time")
    private java.time.LocalTime openTime;

    @Column(name = "close_time")
    private java.time.LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus;

    private double rating;

    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }
}