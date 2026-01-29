package com.example.demo.Model.DTOS.Request;

import lombok.Data;

@Data
public class PricesUpdateRequest {
    private double pricePerSheetBW;
    private double pricePerSheetColor;
    private double priceRingedBinding;
}

