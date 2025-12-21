package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "Restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "cuisine_type", length = 100)
    private String cuisineType;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.pending;

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    // Enum for verification status
    public enum VerificationStatus {
        pending,
        verified,
        rejected;

        public String getValue() {
            return this.name();
        }
    }

    // Relationship with User (optional - if you want bidirectional mapping)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}