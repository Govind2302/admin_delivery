package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    private Restaurant restaurants;
    @OneToOne
    @JoinColumn(name = "address_id", referencedColumnName = "address_id")
    private UserAddresses userAddresses;
    @ManyToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id")
    private Payments payments;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(name = "total_amount")
    private double totalAmount;
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<OrderItem> orderItems = new java.util.ArrayList<>();




    public enum OrderStatus{
        pending, confirmed,out_for_delivery,preparing,delivered, cancelled
    }
}
