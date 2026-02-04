package com.example.demo.Model.Entities;
import lombok.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.Model.Enums.PaymentMethodEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    private String mpPreferenceId;
    private Long mpPaymentId;
    private String mpMerchantOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusEnum paymentStatus;

    @Column(nullable = false)
    private double finalPrice;

    @Column(nullable = false)
    private double depositAmount;

    @Column(nullable = false)
    private Instant orderDate;

    private Instant paidAt;
}

