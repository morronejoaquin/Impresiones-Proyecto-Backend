package com.example.demo.Model.DTOS.Request;
import java.util.UUID;

import com.example.demo.Model.Enums.PaymentMethodEnum;

import lombok.Data;
@Data
public class PaymentCreateRequest {
    private UUID cartId;
    private PaymentMethodEnum paymentMethod;
    private double depositAmount;
}

