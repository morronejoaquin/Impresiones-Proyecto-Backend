package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
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

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody PaymentCreateRequest request){
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Pago creado correctamente");
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
}
