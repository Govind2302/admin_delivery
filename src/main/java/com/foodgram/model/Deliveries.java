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

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name ="delivery_id")
        private int delId;

        @OneToOne
        @JoinColumn(name = "order_id", referencedColumnName = "order_id")
        private Orders orders;

        // Many deliveries can be assigned to one delivery person
        @ManyToOne
        @JoinColumn(name = "delivery_person_id", referencedColumnName = "delivery_person_id", nullable = false)
        private DeliveryPerson deliveryPerson;

        @Column(name ="delivery_time")
        private LocalDateTime delTime;
    }
