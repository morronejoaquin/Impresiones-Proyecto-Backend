package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.PricesUpdateRequest;
import com.example.demo.Model.DTOS.Response.PricesResponse;
import com.example.demo.Services.PricesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "Gestión de precios del sistema")
public class PricesController {

    private final PricesService service;

    @Operation(
            summary = "Actualizar precios",
            description = "Permite actualizar los precios actuales del sistema. Requiere autoridad MODIFICAR_PRECIOS."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Precios actualizados correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos suficientes"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('MODIFICAR_PRECIOS')")
    public ResponseEntity<PricesResponse> updatePrices(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos valores de precios",
                    required = true
            )
            @RequestBody PricesUpdateRequest request
    ){
        PricesResponse updated = service.updatePrices(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    @Operation(
            summary = "Obtener precios actuales",
            description = "Devuelve la configuración de precios vigente"
    )
    @ApiResponse(responseCode = "200", description = "Precios actuales obtenidos correctamente")
    @GetMapping("/current-prices")
    public ResponseEntity<PricesResponse> getCurrentPrices(){
        return ResponseEntity.ok(service.getCurrentPrices());
    }

    @Operation(summary = "Obtener historial de precios")
    @ApiResponse(responseCode = "200", description = "Listado paginado obtenido correctamente")
    @GetMapping
    public ResponseEntity<Page<PricesResponse>> getAll(
            @Parameter(description = "Configuración de paginación")
            Pageable pageable
    ){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Obtener precio por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precio encontrado"),
            @ApiResponse(responseCode = "404", description = "Precio no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PricesResponse> getById(
            @Parameter(description = "UUID del precio", required = true)
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(service.findById(id));
    }
}
