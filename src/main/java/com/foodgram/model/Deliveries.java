package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deliveries {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="delivery_id")
    private int delId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Orders orders;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_person_id",referencedColumnName = "delivery_person_id")
    private DeliveryPerson deliveryPerson;
    @Column(name ="delivery_time")
    private LocalDateTime delTime;

}
