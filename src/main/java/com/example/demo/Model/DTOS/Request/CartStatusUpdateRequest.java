package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartStatusUpdateRequest {

    @NotNull(message = "El estado es obligatorio")
    private OrderStatusEnum status;
}
