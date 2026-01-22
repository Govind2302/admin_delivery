package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payments {
    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int paymentId;
    @OneToOne
    @JoinColumn(name = "order_id",referencedColumnName = "order_id")
    private Orders orders;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Column(name = "amount")
    private double amount;
    @Column(name="transaction_date", insertable=false, updatable=false)
    private LocalDateTime transactionDate;
    public enum PaymentStatus{
        pending,
        completed,
        failed
    }
}
