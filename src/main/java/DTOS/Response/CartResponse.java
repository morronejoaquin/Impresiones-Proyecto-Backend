package DTOS.Response;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.Enums.CartStatusEnum;
import com.example.demo.Enums.OrderStatusEnum;

import lombok.Data;

@Data
public class CartResponse {
    private UUID id;
    private UUID userId;
    private double total;
    private OrderStatusEnum status;
    private CartStatusEnum cartStatus;
    private Instant completedAt;
    private Instant deliveredAt;
    private CustomerDataResponse customer;
}

