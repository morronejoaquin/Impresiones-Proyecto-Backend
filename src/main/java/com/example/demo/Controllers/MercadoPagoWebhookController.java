package com.example.demo.Controllers;

import com.example.demo.Services.MercadoPagoWebhookService;
import com.example.demo.Services.WebhookSignatureValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Tag(
    name = "Webhooks",
    description = "Recepción de notificaciones externas del sistema de pagos"
)
public class MercadoPagoWebhookController {

    private final MercadoPagoWebhookService webhookService;
    private final WebhookSignatureValidator signatureValidator;

    @Operation(
        summary = "Webhook de Mercado Pago",
        description = "Endpoint que recibe notificaciones de eventos de pago enviadas por Mercado Pago. "
                + "Valida la firma y procesa la información del pago."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook recibido correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "Firma inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar webhook")
    })
    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handleWebhook(

            @Parameter(description = "Firma de seguridad enviada por Mercado Pago")
            @RequestHeader(value = "X-Signature", required = false) String signature,

            @Parameter(description = "Identificador único de la notificación")
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,

            @Parameter(description = "ID del recurso asociado al evento (ej: payment ID)")
            @RequestParam(value = "data.id", required = false) String dataIdUrl,

            @Parameter(description = "Cuerpo completo del webhook enviado por Mercado Pago")
            @RequestBody String body
    ) {
        System.out.println(">>> WEBHOOK RECIBIDO: " + requestId);

        webhookService.process(body, signature, requestId, dataIdUrl);

        return ResponseEntity.ok().build();
    }
}
