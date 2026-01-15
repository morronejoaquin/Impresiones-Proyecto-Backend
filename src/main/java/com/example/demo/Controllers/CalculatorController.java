package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PriceCalculationRequest;
import com.example.demo.Model.DTOS.Response.PriceCalculationResponse;
import com.example.demo.Services.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calculator")
public class CalculatorController {

    private final PricingService service;

    public CalculatorController(PricingService service) {
        this.service = service;
    }

    @GetMapping("/calculate")
    public ResponseEntity<PriceCalculationResponse> calculation(@RequestBody PriceCalculationRequest request){
        PriceCalculationResponse response = service.calculadora(request.getPages(), request.getCopies(), request.isColor(), request.getBinding());
        return ResponseEntity.ok(response);
    }
}
