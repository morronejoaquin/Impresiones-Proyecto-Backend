package com.example.demo.Model.DTOS.Response;

import com.example.demo.Model.Enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderSummaryByStatusResponse {
    private OrderStatusEnum status;
    private Long count;
    private Double totalAmount;
}