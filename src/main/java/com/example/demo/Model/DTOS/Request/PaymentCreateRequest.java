package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.PaymentMethodEnum;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentCreateRequest {

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethodEnum paymentMethod;

}

