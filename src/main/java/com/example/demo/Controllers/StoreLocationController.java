package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Response.StoreLocationResponse;
import com.example.demo.Services.StoreLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@Tag(name = "Store", description = "Información pública del local")
public class StoreLocationController {

    private final StoreLocationService service;

    @Operation(
            summary = "Obtener ubicación del local",
            description = "Devuelve la información de ubicación física del comercio"
    )
    @ApiResponse(responseCode = "200", description = "Ubicación obtenida correctamente")
    @GetMapping("/location")
    public ResponseEntity<StoreLocationResponse> getLocation(){
        return ResponseEntity.ok(service.getLocation());
    }

}


