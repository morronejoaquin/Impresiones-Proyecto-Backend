package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.BindingTypeEnum;
import lombok.Data;

@Data
public class OrderItemUpdateRequest {
    private Integer copies;
    private Boolean color;
    private Boolean doubleSided;
    private BindingTypeEnum binding;
    private String comments;
}
