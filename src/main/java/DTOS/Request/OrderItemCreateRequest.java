package DTOS.Request;

import java.util.UUID;

import com.example.demo.Enums.BindingTypeEnum;

import lombok.Data;

@Data
public class OrderItemCreateRequest {
    private UUID cartId;
    private boolean isColor;
    private boolean isDoubleSided;
    private BindingTypeEnum binding;
    private int pages;
    private String comments;
    private String file;
    private int copies;
}

