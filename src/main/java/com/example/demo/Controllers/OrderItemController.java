package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.OrderItemUpdateRequest;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Services.OrderItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orderItems")
public class OrderItemController {

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<OrderItemResponse>> getAll(Pageable pageable){
        Page<OrderItemResponse> items = service.findAll(pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getById(@PathVariable UUID id){
        OrderItemResponse item = service.findById(id);
        return ResponseEntity.ok(item);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderItemResponse> update(@PathVariable UUID id, @RequestBody OrderItemUpdateRequest request){
        OrderItemResponse updated = service.update(id, request);
        return ResponseEntity.ok(updated);
    }
}
