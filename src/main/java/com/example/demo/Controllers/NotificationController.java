package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Response.NotificationResponse;
import com.example.demo.Services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(
    name = "Notificaciones",
    description = "Gestión de notificaciones del usuario"
)
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
        summary = "Obtener notificaciones no leídas",
        description = "Devuelve la lista de notificaciones pendientes de lectura del usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnread(Authentication authentication) {
        return ResponseEntity.ok(
                notificationService.getUnreadForUser(authentication.getName())
        );
    }

    @Operation(
        summary = "Marcar notificación como leída",
        description = "Marca una notificación específica como leída"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación actualizada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID de la notificación")
            @PathVariable UUID id) {

        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Marcar todas las notificaciones como leídas",
        description = "Marca como leídas todas las notificaciones del usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificaciones actualizadas"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.ok().build();
    }
}
