package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.BindingTypeEnum;
import lombok.Data;

@Data
public class PriceCalculationRequest {
    private int pages;
    private int copies;
    private boolean color;
    private BindingTypeEnum binding;
}
