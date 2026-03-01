package com.example.demo.Model.DTOS.Response;

import lombok.Data;

@Data
public class PriceCalculationResponse {
    private double total;
    private double pricePerSheet;
    private boolean isDoubleSided;
    private double bindingPrice;
}
