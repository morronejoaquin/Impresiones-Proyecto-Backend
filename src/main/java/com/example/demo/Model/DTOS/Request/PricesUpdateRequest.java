package com.example.demo.Model.DTOS.Request;

import lombok.Data;

import java.time.Instant;

@Data
public class PricesUpdateRequest {
    private double pricePerSheetBW;
    private double pricePerSheetColor;
    private double priceRingedBinding;
    private Instant validFrom;
    private Instant validTo;
}

