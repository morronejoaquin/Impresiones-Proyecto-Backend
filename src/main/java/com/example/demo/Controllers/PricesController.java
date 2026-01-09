package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PricesUpdateRequest;
import com.example.demo.Model.DTOS.Response.PricesResponse;
import com.example.demo.Services.PricesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/prices")
public class PricesController {

    private final PricesService service;

    public PricesController(PricesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody PricesUpdateRequest request){
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("¨Precios nuevos creados correctamente");
    }

    @GetMapping
    public ResponseEntity<Page<PricesResponse>> getAll(Pageable pageable){
        Page<PricesResponse> prices = service.findAll(pageable);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricesResponse> getById(@PathVariable UUID id){
        PricesResponse price = service.findById(id);
        return ResponseEntity.ok(price);
    }
}
