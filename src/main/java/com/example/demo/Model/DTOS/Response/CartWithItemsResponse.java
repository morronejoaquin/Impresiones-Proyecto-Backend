package com.example.demo.Model.DTOS.Response;

import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.OrderStatusEnum;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class CartWithItemsResponse {
    private UUID id;
    private UUID userId;
    private double total;
    private CustomerDataResponse customer;
    private OrderStatusEnum status;
    private CartStatusEnum cartStatus;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private Instant completedAt;
    private Instant deliveredAt;
    private Instant admReceivedAt;
    private List<OrderItemResponse> items;
    private boolean deleted = false;
}
