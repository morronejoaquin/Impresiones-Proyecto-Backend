package com.example.demo.Model.DTOS.Request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PricesUpdateRequest {

    @Positive(message = "El precio por hoja blanco y negro debe ser mayor a 0")
    private double pricePerSheetBW;

    @Positive(message = "El precio por hoja color debe ser mayor a 0")
    private double pricePerSheetColor;

    @Positive(message = "El precio de anillado debe ser mayor a 0")
    private double priceRingedBinding;
}

