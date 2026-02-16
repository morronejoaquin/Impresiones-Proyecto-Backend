package com.example.demo.Model.DTOS.Response;

import com.example.demo.Model.Enums.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartHistoryResponse {
    private UUID cartId;
    private Instant createdAt;
    private OrderStatusEnum status;
    private double total;

    private String paymentMethod;
    private String paymentStatus;

    private List<OrderItemResponse> items;
}
