package com.example.demo.Controllers;

import com.example.demo.Services.MercadoPagoWebhookService;
import com.example.demo.Services.WebhookSignatureValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

    private final MercadoPagoWebhookService webhookService;
    private final WebhookSignatureValidator signatureValidator;

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(value = "X-Signature", required = false) String signature,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId,
            @RequestParam(value = "data.id", required = false) String dataIdUrl,
            @RequestBody String body
    ) {
        System.out.println(">>> WEBHOOK RECIBIDO: " + requestId);

        webhookService.process(body, signature, requestId, dataIdUrl);

        return ResponseEntity.ok().build();
    }
}
