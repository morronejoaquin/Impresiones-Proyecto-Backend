package com.example.demo.Model.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryByMethodResponse {
    private String paymentMethod;
    private double totalAmount;
    private int transactionCount;
    private double percentage;
}
