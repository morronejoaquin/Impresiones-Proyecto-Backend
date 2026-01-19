package com.example.demo.Model.DTOS.Response;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.OrderStatusEnum;

import lombok.Data;

@Data
public class CartResponse {
    private UUID id;
    private UUID userId;
    private double total;
    private CustomerDataResponse customer;
    private OrderStatusEnum status;
    private CartStatusEnum cartStatus;
    private Instant completedAt;
    private Instant deliveredAt;
    private Instant admReceivedAt;
    private boolean deleted = false;
}

