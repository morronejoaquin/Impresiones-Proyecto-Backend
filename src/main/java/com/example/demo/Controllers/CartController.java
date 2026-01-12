package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Services.CartService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CartResponse> save(@RequestBody CartCreateRequest request){
        CartResponse saved = service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<Page<CartResponse>> getAll(Pageable pageable){
        Page<CartResponse> carts = service.findAll(pageable);
        return ResponseEntity.ok(carts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getById(@PathVariable UUID id){
        CartResponse cart = service.findById(id);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/{cartId}/agregar-item")
    public ResponseEntity<String> agregar(@PathVariable UUID cartId, @RequestBody OrderItemCreateRequest request){
        String mensaje = service.agregar(cartId, request);
        return ResponseEntity.ok(mensaje);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
public ResponseEntity<Void> eliminarItem(
        @PathVariable UUID cartId,
        @PathVariable UUID itemId){

    service.eliminarItem(cartId, itemId);
    return ResponseEntity.noContent().build();
}

}
