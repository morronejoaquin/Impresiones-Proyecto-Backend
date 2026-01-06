package com.example.demo.Model.DTOS.Response;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.Model.Enums.PaymentMethodEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;

import lombok.Data;

@Data
public class PaymentResponse {
    private UUID id;
    private UUID cartId;
    private double finalPrice;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum paymentStatus;
    private Instant orderDate;
}

