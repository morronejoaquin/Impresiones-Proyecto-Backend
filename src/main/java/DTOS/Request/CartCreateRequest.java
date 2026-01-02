package DTOS.Request;

import java.util.UUID;

import lombok.Data;

@Data
public class CartCreateRequest {
    private UUID userId;
    private CustomerDataRequest customer;
}
