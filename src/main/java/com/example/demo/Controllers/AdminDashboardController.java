package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Response.AdminDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.AdminDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Tag(
    name = "Administración",
    description = "Endpoints relacionados al panel administrativo"
)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @Operation(
        summary = "Obtener datos del dashboard",
        description = "Devuelve métricas y estadísticas del panel administrativo. "
                + "Permite filtrar opcionalmente por rango de fechas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso prohibido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('VER_ESTADISTICAS')")
    public ResponseEntity<AdminDashboardResponse> getDashboardData(

            @Parameter(description = "Fecha de inicio del filtro en formato YYYY-MM-DD")
            @RequestParam(required = false) String startDate,

            @Parameter(description = "Fecha de fin del filtro en formato YYYY-MM-DD")
            @RequestParam(required = false) String endDate

    ){
        AdminDashboardResponse data = adminDashboardService.getDashboardData(startDate, endDate);
        return ResponseEntity.ok(data);
    }
}
