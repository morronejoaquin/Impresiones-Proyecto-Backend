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
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum paymentStatus;

    private double finalPrice;
    private double depositAmount;

    private Instant orderDate;
}

