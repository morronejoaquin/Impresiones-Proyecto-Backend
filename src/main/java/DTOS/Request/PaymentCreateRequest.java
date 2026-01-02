package DTOS.Request;
import java.util.UUID;

import com.example.demo.Enums.PaymentMethodEnum;

import lombok.Data;
@Data
public class PaymentCreateRequest {
    private UUID cartId;
    private PaymentMethodEnum paymentMethod;
    private double depositAmount;
}

