package com.example.demo.Model.DTOS.Response;
import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class PricesResponse {
    private UUID id;
    private double pricePerSheetBW;
    private double pricePerSheetColor;
    private double priceRingedBinding;
    private double priceStapledBinding;

    private Instant validFrom;
    private Instant validTo;
}

