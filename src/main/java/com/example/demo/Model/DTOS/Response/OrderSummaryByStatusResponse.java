package com.example.demo.Model.DTOS.Response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderSummaryByStatusResponse {
    private String status;
    private int count;
    private double totalAmount;

    public OrderSummaryByStatusResponse(String status, int count, double totalAmount) {
        this.status = status;
        this.count = count;
        this.totalAmount = totalAmount;
    }

}