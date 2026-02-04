package com.example.demo.Model.DTOS.Response;

import com.example.demo.Model.Enums.PaymentMethodEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class PaymentHistoryResponse {
    private UUID id;
    private UUID cartId;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum paymentStatus;
    private double finalPrice;
    private Instant orderDate;
    private Instant paidAt;
}
