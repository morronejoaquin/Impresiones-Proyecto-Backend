package DTOS.Response;

import java.util.UUID;

import com.example.demo.Enums.BindingTypeEnum;

import lombok.Data;

@Data
public class OrderItemResponse {
    private UUID id;
    private int pages;
    private int copies;
    private double amount;
    private boolean isColor;
    private boolean isDoubleSided;
    private BindingTypeEnum binding;
    private String comments;
}
