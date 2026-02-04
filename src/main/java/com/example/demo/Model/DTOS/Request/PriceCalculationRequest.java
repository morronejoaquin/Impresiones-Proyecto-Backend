package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.BindingTypeEnum;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PriceCalculationRequest {

    @Min(value = 1, message = "La cantidad de páginas debe ser al menos 1")
    private int pages;

    @Min(value = 1, message = "La cantidad de copias debe ser al menos 1")
    private int copies;

    private boolean color;

    private BindingTypeEnum binding;
}
