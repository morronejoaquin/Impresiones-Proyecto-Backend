package com.example.demo.Model.DTOS.Response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String action;
    private String checkoutUrl;
    private String message;
    private UUID cartId;
}

