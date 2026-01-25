package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "delivery_person_id")
    private Long deliveryPersonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status = DeliveryStatus.picked_up;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    public enum DeliveryStatus {
        picked_up,
        in_transit,
        delivered,
        failed_attempt;

        public String getValue() {
            return this.name();
        }
    }
}