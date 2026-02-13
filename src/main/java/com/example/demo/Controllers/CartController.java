package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.CartStatusUpdateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartWithItemsResponse;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Model.Enums.AdminDateFilterType;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
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
    @PreAuthorize("hasAuthority('CREAR_CARRITO')")
    public ResponseEntity<CartResponse> save(Authentication authentication){
        CartResponse saved = service.save(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('VER_TODOS_PEDIDOS')")
    public ResponseEntity<Page<CartWithItemsResponse>> getAllWithItems(Pageable pageable){
        Page<CartWithItemsResponse> carts = service.findAllWithItems(pageable);
        return ResponseEntity.ok(carts);
    }

    @GetMapping
    public ResponseEntity<Page<CartResponse>> getAll(Pageable pageable){
        Page<CartResponse> carts = service.findAll(pageable);
        return ResponseEntity.ok(carts);
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<CartResponse>> getPendingCarts(Pageable pageable){
        Page<CartResponse> pendingCarts = service.findByStatus(
            com.example.demo.Model.Enums.OrderStatusEnum.PENDING,
            pageable
        );
        return ResponseEntity.ok(pendingCarts);
    }

    // Formato para enviar filtros 
    // /delivered?date=2025-01-10T00:00:00Z&dateType=DELIVERED_AT
    // /delivered?date=2025-01-10T00:00:00Z&dateType=ADM_RECEIVED_AT
    @GetMapping("/delivered")
    public ResponseEntity<Page<CartResponse>> getDeliveredCarts(
            @RequestParam(required = false) Instant date,
            @RequestParam(required = false) AdminDateFilterType dateType,
            Pageable pageable
    ) {
        Page<CartResponse> result = service.findDeliveredForAdmin(
                date,
                dateType,
                pageable
        );
        return ResponseEntity.ok(result);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getById(@PathVariable UUID id){
        CartResponse cart = service.findById(id);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping(value = "/items/agregar-orden",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CARGAR_PEDIDO')")
    public ResponseEntity<OrderItemResponse> agregar(@RequestPart("data") String data, @RequestPart("file") MultipartFile file, Authentication authentication) throws Exception{
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

        OrderItemResponse agregado = service.agregar(request, driveFileId, file.getOriginalFilename(), metadata, authentication.getName());
        return ResponseEntity.ok(agregado);
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('ELIMINAR_PEDIDO')")
    public ResponseEntity<String> eliminarItem(
            @PathVariable UUID itemId,
            Authentication authentication) throws Exception{

        service.eliminarItem(itemId, authentication.getName());
        return ResponseEntity.ok().body("Item eliminado correctamente");
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<CartWithItemsResponse> findWithItems(@PathVariable UUID id){
        CartWithItemsResponse cart = service.findWithItems(id);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/my-cart")
    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public ResponseEntity<CartWithItemsResponse> findOpenCart(Authentication authentication){
        CartWithItemsResponse cart = service.findOpenCart(authentication.getName());
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/{cartId}/estado")
    public ResponseEntity<CartWithItemsResponse> actualizarEstado(
            @PathVariable UUID cartId,
            @RequestBody CartStatusUpdateRequest request){

        CartWithItemsResponse response =
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

    @GetMapping("/{carritoId}/ordenes")
    public ResponseEntity<List<OrderItemResponse>> obtenerOrdenesPorCarrito(
            @PathVariable UUID carritoId){
        List<OrderItemResponse> ordenes = service.obtenerOrdenesPorCarrito(carritoId);
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/{carritoId}/ordenes/{ordenId}")
    public ResponseEntity<OrderItemResponse> obtenerOrdenEspecificaPorCarrito(
            @PathVariable UUID carritoId,
            @PathVariable UUID ordenId){
        OrderItemResponse orden = service.obtenerOrdenEspecificaPorCarrito(carritoId, ordenId);
        return ResponseEntity.ok(orden);
    }

    @GetMapping("/{carritoId}/ordenes/{ordenId}/descargar")
    public ResponseEntity<byte[]> descargarArchivoOrden(
            @PathVariable UUID carritoId,
            @PathVariable UUID ordenId) throws Exception {
        
        // Obtener la orden específica
        OrderItemResponse orden = service.obtenerOrdenEspecificaPorCarrito(carritoId, ordenId);

        // Descargar el archivo desde Google Drive
        try (InputStream fileStream = googleDriveService.descargarArchivo(orden.getDriveFileId())) {
            byte[] fileBytes = fileStream.readAllBytes();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + orden.getFileName() + "\"")
                    .header("Content-Type", orden.getFileType().getMimeType())
                    .body(fileBytes);
        }
    }
    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public ResponseEntity<Page<CartResponse>> obtenerMisPedidos(
            Authentication authentication,
            Pageable pageable) {
        Page<CartResponse> pedidos = service.obtenerPedidosDelUsuario(authentication.getName(), pageable);
        return ResponseEntity.ok(pedidos);
    }


@GetMapping("/admin/orders")
@PreAuthorize("hasRole('administrador')")
public ResponseEntity<Page<CartResponse>> getActiveCartsForAdmin(Pageable pageable){
    Page<CartResponse> carts = service.getActiveCartsForAdmin(pageable);
    return ResponseEntity.ok(carts);
}

@GetMapping("/admin/filter")
@PreAuthorize("hasRole('administrador')")
public ResponseEntity<Page<CartResponse>> filterCartsForAdmin(
        @RequestParam(required = false) OrderStatusEnum status,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String customerEmail,
        Pageable pageable
){
    Page<CartResponse> carts = service.filterCartsForAdmin(status, startDate, endDate, customerEmail, pageable);
    return ResponseEntity.ok(carts);
}
}