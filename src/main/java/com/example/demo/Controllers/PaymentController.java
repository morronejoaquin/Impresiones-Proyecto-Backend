package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Response.CartHistoryResponse;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
import com.example.demo.Services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<CartHistoryResponse>> getAll(Pageable pageable){
        Page<CartHistoryResponse> payments = service.findAll(pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartHistoryResponse> getById(@PathVariable UUID id){
        CartHistoryResponse payment = service.findById(id);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponse> checkout(@RequestBody PaymentCreateRequest dto, Authentication authentication) {
        PaymentResponse response = service.processCheckout(dto, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
