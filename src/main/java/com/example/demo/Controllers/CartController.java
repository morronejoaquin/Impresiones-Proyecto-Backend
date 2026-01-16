package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartWithItemsResponse;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Services.CartService;
import com.example.demo.Services.GoogleDriveService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService service;
    private final GoogleDriveService googleDriveService;

    public CartController(CartService service, GoogleDriveService googleDriveService) {
        this.service = service;
        this.googleDriveService = googleDriveService;
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

    @PatchMapping(value = "/{cartId}/agregar-item",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderItemResponse> agregar(@PathVariable UUID cartId, @RequestPart("data") String data, @RequestPart("file") MultipartFile file) throws Exception{
        validarFormato(file);

        ObjectMapper mapper = new ObjectMapper();
        OrderItemCreateRequest request =
                mapper.readValue(data, OrderItemCreateRequest.class);

        String driveFileId = googleDriveService.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getContentType());

        OrderItemResponse agregado = service.agregar(cartId, request, driveFileId, file.getOriginalFilename());
        return ResponseEntity.ok(agregado);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<String> eliminarItem(
            @PathVariable UUID cartId,
            @PathVariable UUID itemId){

        service.eliminarItem(cartId, itemId);
        return ResponseEntity.ok().body("Item eliminado correctamente");
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<CartWithItemsResponse> findWithItems(@PathVariable UUID id){
        CartWithItemsResponse cart = service.findWithItems(id);
        return ResponseEntity.ok(cart);
    }

    private void validarFormato(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.matches(".*\\.(pdf|jpg|png)$")) {
            throw new IllegalArgumentException("Formato no permitido");
        }
    }

}
