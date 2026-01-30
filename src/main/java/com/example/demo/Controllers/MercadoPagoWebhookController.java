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
            @RequestHeader("X-Signature") String signature,
            @RequestHeader("X-Request-Id") String requestId,
            @RequestBody String payload
    ) {

        boolean valid = signatureValidator.isValid(signature, requestId, payload);

        if (!valid) {
            return ResponseEntity.status(403).build();
        }

        webhookService.process(payload);
        return ResponseEntity.ok().build();
    }
}
