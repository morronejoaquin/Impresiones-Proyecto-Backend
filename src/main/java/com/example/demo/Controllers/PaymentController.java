package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Response.PaymentPreferenceResponse;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
import com.example.demo.Services.MercadoPagoService;
import com.example.demo.Services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;
    private final MercadoPagoService mercadoPagoService;

    public PaymentController(PaymentService service, MercadoPagoService mercadoPagoService) {
        this.service = service;
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> save(@RequestBody PaymentCreateRequest request){
        PaymentResponse payment = service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getAll(Pageable pageable){
        Page<PaymentResponse> payments = service.findAll(pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id){
        PaymentResponse payment = service.findById(id);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/preference/{cartId}")
    public ResponseEntity<PaymentPreferenceResponse> createPreference(@PathVariable UUID cartId) {
        PaymentPreferenceResponse response = mercadoPagoService.createPreference(cartId);
        return ResponseEntity.ok(response);
    }
}
