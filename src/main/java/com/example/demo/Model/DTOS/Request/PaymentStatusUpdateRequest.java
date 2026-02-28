package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.PaymentStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentStatusUpdateRequest {

    @NotNull(message = "El estado es obligatorio")
    private PaymentStatusEnum status;
}
