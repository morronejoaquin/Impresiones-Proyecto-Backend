package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.OrderItemUpdateRequest;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Services.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orderItems")
@RequiredArgsConstructor
@Tag(name = "Order Items", description = "Gestión de ítems de órdenes")
public class OrderItemController {

    private final OrderItemService service;

    
    @Operation(
            summary = "Obtener todos los ítems",
            description = "Devuelve una lista paginada de ítems de órdenes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    @PreAuthorize("authenticated")
    public ResponseEntity<Page<OrderItemResponse>> getAll(
            @Parameter(description = "Configuración de paginación")
            Pageable pageable
    ){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
            summary = "Obtener ítem por ID",
            description = "Devuelve un ítem específico mediante su UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("authenticated")
    public ResponseEntity<OrderItemResponse> getById(
            @Parameter(description = "UUID del ítem", required = true)
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Actualizar ítem",
            description = "Actualiza parcialmente un ítem de orden"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ítem actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ACTUALIZAR_ORDEN')")
    public ResponseEntity<OrderItemResponse> update(
            @Parameter(description = "UUID del ítem a actualizar", required = true)
            @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos a modificar",
                    required = true
            )
            @RequestBody OrderItemUpdateRequest request
    ){
        return ResponseEntity.ok(service.update(id, request));
    }
}
