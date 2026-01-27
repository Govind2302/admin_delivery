package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "delivery_person_id", referencedColumnName = "delivery_person_id")
    private DeliveryPerson deliveryPerson;


    public Long getDeliveryPersonId() {
        return Long.valueOf(deliveryPerson != null ? deliveryPerson.getDeliveryPersonId() : null);
    }


    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}