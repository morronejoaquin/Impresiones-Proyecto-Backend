package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Request.PaymentStatusUpdateRequest;
import com.example.demo.Model.DTOS.Response.CartHistoryResponse;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
import com.example.demo.Services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Gestión de pagos y checkout")
public class PaymentController {

    private final PaymentService service;

    @Operation(
            summary = "Obtener historial de pagos",
            description = "Devuelve una lista paginada del historial de pagos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<Page<CartHistoryResponse>> getAll(
            @Parameter(description = "Configuración de paginación")
            Pageable pageable
    ){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(
            summary = "Obtener pago por ID",
            description = "Devuelve la información de un pago específico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CartHistoryResponse> getById(
            @Parameter(description = "UUID del pago", required = true)
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
            summary = "Procesar checkout",
            description = "Genera un pago a partir del carrito del usuario autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago procesado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos enviados"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PostMapping("/checkout")
    public ResponseEntity<PaymentResponse> checkout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear el pago",
                    required = true
            )
            @RequestBody PaymentCreateRequest dto,
            @Parameter(hidden = true)
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                service.processCheckout(dto, authentication.getName())
        );
    }

    @Operation(summary = "Actualizar estado del pago")
    @PatchMapping("/{cartId}/update-status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @Parameter(description = "ID del carrito")
            @PathVariable UUID cartId,
            @RequestBody PaymentStatusUpdateRequest dto
    ){
        return ResponseEntity.ok(service.updatePaymentStatus(cartId, dto));
    }
}
