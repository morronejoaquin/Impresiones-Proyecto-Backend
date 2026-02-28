package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.CartStatusUpdateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.*;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(name = "Carritos", description = "Gestión de carritos, pedidos y órdenes")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService service;
    private final GoogleDriveService googleDriveService;

    public CartController(CartService service, GoogleDriveService googleDriveService) {
        this.service = service;
        this.googleDriveService = googleDriveService;
    }

    @Operation(summary = "Crear carrito")
    @ApiResponse(responseCode = "201", description = "Carrito creado correctamente")
    @PostMapping
    @PreAuthorize("hasAuthority('CREAR_CARRITO')")
    public ResponseEntity<CartResponse> save(Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(authentication.getName()));
    }

    @Operation(summary = "Listar todos los carritos con items (Admin)")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('VER_TODOS_PEDIDOS')")
    public ResponseEntity<Page<CartWithItemsResponse>> getAllWithItems(Pageable pageable){
        return ResponseEntity.ok(service.findAllWithItems(pageable));
    }

    @Operation(summary = "Listar carritos")
    @GetMapping
    public ResponseEntity<Page<CartResponse>> getAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Listar carritos pendientes")
    @GetMapping("/pending")
    public ResponseEntity<Page<CartResponse>> getPendingCarts(Pageable pageable){
        return ResponseEntity.ok(
                service.findByStatus(OrderStatusEnum.PENDING, pageable)
        );
    }

    @Operation(summary = "Filtrar carritos entregados")
    @GetMapping("/delivered")
    public ResponseEntity<Page<CartResponse>> getDeliveredCarts(
            @Parameter(description = "Fecha filtro ISO-8601")
            @RequestParam(required = false) Instant date,
            @Parameter(description = "Tipo de fecha del filtro")
            @RequestParam(required = false) AdminDateFilterType dateType,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.findDeliveredForAdmin(date, dateType, pageable)
        );
    }

    @Operation(summary = "Obtener carrito por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getById(
            @Parameter(description = "ID del carrito")
            @PathVariable UUID id){
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Agregar orden a carrito",
            description = "Sube archivo y datos en formato multipart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden agregada"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido")
    })
    @PatchMapping(value = "/items/agregar-orden",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CARGAR_PEDIDO')")
    public ResponseEntity<OrderItemResponse> agregar(
            @RequestPart("data") String data,
            @RequestPart("file") MultipartFile file,
            Authentication authentication) throws Exception{

        service.validarFormato(file);

        ObjectMapper mapper = new ObjectMapper();
        OrderItemCreateRequest request =
                mapper.readValue(data, OrderItemCreateRequest.class);

        byte[] bytes = file.getBytes();

        FileMetaData metadata = FileUtils.obtenerMetadata(
                new ByteArrayInputStream(bytes),
                file.getContentType()
        );

        String driveFileId = googleDriveService.uploadFile(
                file.getOriginalFilename(),
                new ByteArrayInputStream(bytes),
                file.getContentType()
        );

        OrderItemResponse agregado =
                service.agregar(request, driveFileId,
                        file.getOriginalFilename(),
                        metadata,
                        authentication.getName());

        return ResponseEntity.ok(agregado);
    }

    @Operation(summary = "Eliminar orden")
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasAuthority('ELIMINAR_PEDIDO')")
    public ResponseEntity<String> eliminarItem(
            @Parameter(description = "ID del item")
            @PathVariable UUID itemId,
            Authentication authentication) throws Exception{

        service.eliminarItem(itemId, authentication.getName());
        return ResponseEntity.ok("Item eliminado correctamente");
    }

    @Operation(summary = "Obtener carrito con items")
    @GetMapping("/{id}/items")
    public ResponseEntity<CartWithItemsResponse> findWithItems(
            @Parameter(description = "ID del carrito")
            @PathVariable UUID id){
        return ResponseEntity.ok(service.findWithItems(id));
    }

    @Operation(summary = "Obtener carrito abierto del usuario")
    @GetMapping("/my-cart")
    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public ResponseEntity<CartWithItemsResponse> findOpenCart(Authentication authentication){
        return ResponseEntity.ok(service.findOpenCart(authentication.getName()));
    }

    @Operation(summary = "Actualizar estado del carrito")
    @PatchMapping("/{cartId}/estado")
    public ResponseEntity<CartWithItemsResponse> actualizarEstado(
            @Parameter(description = "ID del carrito")
            @PathVariable UUID cartId,
            @RequestBody CartStatusUpdateRequest request){

        return ResponseEntity.ok(
                service.actualizarEstado(cartId, request.getStatus())
        );
    }

    @Operation(summary = "Filtrar carritos")
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

    @Operation(summary = "Obtener órdenes por carrito")
    @GetMapping("/{carritoId}/ordenes")
    public ResponseEntity<List<OrderItemResponse>> obtenerOrdenesPorCarrito(
            @PathVariable UUID carritoId){
        return ResponseEntity.ok(
                service.obtenerOrdenesPorCarrito(carritoId)
        );
    }

    @Operation(summary = "Obtener orden específica por carrito")
    @GetMapping("/{carritoId}/ordenes/{ordenId}")
    public ResponseEntity<OrderItemResponse> obtenerOrdenEspecificaPorCarrito(
            @PathVariable UUID carritoId,
            @PathVariable UUID ordenId){
        return ResponseEntity.ok(
                service.obtenerOrdenEspecificaPorCarrito(carritoId, ordenId)
        );
    }

    @Operation(summary = "Descargar archivo de orden")
    @GetMapping("/{carritoId}/ordenes/{ordenId}/descargar")
    public ResponseEntity<byte[]> descargarArchivoOrden(
            @PathVariable UUID carritoId,
            @PathVariable UUID ordenId) throws Exception {

        OrderItemResponse orden =
                service.obtenerOrdenEspecificaPorCarrito(carritoId, ordenId);

        try (InputStream fileStream =
                     googleDriveService.descargarArchivo(orden.getDriveFileId())) {

            byte[] fileBytes = fileStream.readAllBytes();

            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + orden.getFileName() + "\"")
                    .header("Content-Type",
                            orden.getFileType().getMimeType())
                    .body(fileBytes);
        }
    }

    @Operation(summary = "Historial de pedidos del usuario")
    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('VER_CARRITO')")
    public ResponseEntity<Page<CartHistoryResponse>> obtenerMisPedidos(
            Authentication authentication,
            Pageable pageable) {
        return ResponseEntity.ok(
                service.obtenerPedidosDelUsuario(authentication.getName(), pageable)
        );
    }

    @Operation(summary = "Carritos activos para administrador")
    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<Page<CartResponse>> getActiveCartsForAdmin(Pageable pageable){
        return ResponseEntity.ok(
                service.getActiveCartsForAdmin(pageable)
        );
    }

    @Operation(summary = "Filtrar carritos (Admin)")
    @GetMapping("/admin/filter")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<Page<CartResponse>> filterCartsForAdmin(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String customerEmail,
            Pageable pageable
    ){
        return ResponseEntity.ok(
                service.filterCartsForAdmin(status, startDate, endDate, customerEmail, pageable)
        );
    }

    @Operation(summary = "Historial de entregados (Admin)")
    @GetMapping("/admin/history")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<Page<CartResponse>> getDeliveredHistory(
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable
    ){
        return ResponseEntity.ok(
                service.getDeliveredHistory(customerEmail, startDate, endDate, pageable)
        );
    }
}