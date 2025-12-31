package com.example.demo.Entities;
import lombok.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.Enums.CartStatusEnum;
import com.example.demo.Enums.OrderStatusEnum;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private UserEntity user;

    private double total;

    @Embedded
    private CustomerDataEntity customer;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Enumerated(EnumType.STRING)
    private CartStatusEnum cartStatus;

    private Instant completedAt;
    private Instant deliveredAt;
}

