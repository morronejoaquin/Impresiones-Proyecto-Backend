package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.OrderStatusEnum;
import lombok.Data;

@Data
public class CartStatusUpdateRequest {
    private OrderStatusEnum status;
}
