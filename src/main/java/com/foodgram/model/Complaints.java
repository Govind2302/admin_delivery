package com.foodgram.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Complaints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private int complaintId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Orders order;

    @Column(name = "complaint", columnDefinition = "TEXT")
    private String complaint;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ComplaintStatus status;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    public enum ComplaintStatus {
        OPEN,
        RESOLVED,
        CLOSED
    }
}