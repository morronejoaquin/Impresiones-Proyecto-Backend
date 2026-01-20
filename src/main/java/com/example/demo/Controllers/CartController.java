package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.CartStatusUpdateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartWithItemsResponse;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Model.Enums.OrderStatusEnum;
import com.example.demo.Services.CartService;
import com.example.demo.Services.GoogleDriveService;
import com.example.demo.Utils.FileMetaData;
import com.example.demo.Utils.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.time.LocalDate;
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

    @PatchMapping("/close/{id}")
    public ResponseEntity<CartResponse> closeCart (@PathVariable UUID id){
        CartResponse cart = service.closeCart(id);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<CartResponse>> getPendingCarts(Pageable pageable){
        Page<CartResponse> pendingCarts = service.findByStatus(
            com.example.demo.Model.Enums.OrderStatusEnum.PENDING,
            pageable
        );
        return ResponseEntity.ok(pendingCarts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getById(@PathVariable UUID id){
        CartResponse cart = service.findById(id);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping(value = "/{cartId}/agregar-item",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OrderItemResponse> agregar(@PathVariable UUID cartId, @RequestPart("data") String data, @RequestPart("file") MultipartFile file) throws Exception{
        service.validarFormato(file);

        ObjectMapper mapper = new ObjectMapper();
        OrderItemCreateRequest request =
                mapper.readValue(data, OrderItemCreateRequest.class);

        byte[] bytes = file.getBytes();

        FileMetaData metadata = FileUtils.obtenerMetadata(
                new ByteArrayInputStream(bytes),
                file.getContentType()
        );

        String driveFileId = googleDriveService.uploadFile(file.getOriginalFilename(), new ByteArrayInputStream(bytes), file.getContentType());

        OrderItemResponse agregado = service.agregar(cartId, request, driveFileId, file.getOriginalFilename(), metadata);
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

    @GetMapping("/{userId}/open")
    public ResponseEntity<CartWithItemsResponse> findOpenCart(@PathVariable UUID userId){
        CartWithItemsResponse cart = service.findOpenCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/{cartId}/estado")
    public ResponseEntity<CartResponse> actualizarEstado(
            @PathVariable UUID cartId,
            @RequestBody CartStatusUpdateRequest request){

        CartResponse response =
                service.actualizarEstado(cartId, request.getStatus());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public Page<CartResponse> filterCarts(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) UUID userId,
            Pageable pageable
    ){
        return service.filterCarts(status, from, to, userId, pageable);
    }

}
