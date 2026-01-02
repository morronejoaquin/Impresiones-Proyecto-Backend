package DTOS.Response;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.Enums.PaymentMethodEnum;
import com.example.demo.Enums.PaymentStatusEnum;

import lombok.Data;

@Data
public class PaymentResponse {
    private UUID id;
    private double finalPrice;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum paymentStatus;
    private Instant orderDate;
}

