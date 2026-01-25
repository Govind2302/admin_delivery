package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "delivery_person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "vehicle_number", length = 50)
    private String vehicleNumber;

    @Column(name = "operating_area", length = 255)
    private String operatingArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.pending;

    @Column(name = "earnings", precision = 10, scale = 2)
    private BigDecimal earnings = BigDecimal.ZERO;

    // Add this for backward compatibility with your existing code
    @Transient
    private User user;

    // Helper method to set user and extract userId
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getUserId();
        }
    }

    // Helper to set status from string
    public void setStatus(VerificationStatus status) {
        this.verificationStatus = status;
    }

    public VerificationStatus getStatus() {
        return this.verificationStatus;
    }

    public enum VerificationStatus {
        pending,
        verified,
        rejected;

        public String getValue() {
            return this.name();
        }
    }
}