package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PriceCalculationRequest;
import com.example.demo.Model.DTOS.Response.PriceCalculationResponse;
import com.example.demo.Services.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(
    name = "Calculadora",
    description = "Endpoints para el cálculo de precios de impresiones"
)
@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final PricingService service;

    public CalculatorController(PricingService service) {
        this.service = service;
    }

    @Operation(
        summary = "Calcular precio de impresión",
        description = "Calcula el precio total de una impresión según cantidad de páginas, "
                + "copias, tipo de impresión (color o blanco y negro) y tipo de encuadernado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculo realizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculation(
            @RequestBody PriceCalculationRequest request){

        PriceCalculationResponse response = service.calculadora(
                request.getPages(),
                request.getCopies(),
                request.isColor(),
                request.isDoubleSided(),
                request.getBinding()
        );

        return ResponseEntity.ok(response);
    }
}