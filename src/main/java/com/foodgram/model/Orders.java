package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="order_id")
    private int orderId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id")
    private Restaurants restaurants;
    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    private UserAddresses userAddresses;
    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id")
    private Payments payments;
    @OneToOne
    @JoinColumn(name = "delivery_person_id",referencedColumnName = "delivery_person_id")
    private DeliveryPerson deliveryPerson;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(name = "total_amount")
    private double totalAmount;
    @Column(name = "order_date")
    private LocalDateTime orderDate;


    public enum OrderStatus{
        pending, confirmed,preparing,delivered, cancelled
    }
}
