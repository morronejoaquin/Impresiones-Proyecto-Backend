package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class WebhookSignatureValidator {

    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    public boolean isValid(String signatureHeader, String requestId, String dataId) {
        try {
            String ts = "";
            String receivedHash = "";

            String[] parts = signatureHeader.split(",");
            for (String part : parts) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim();
                    String value = kv[1].trim();
                    if (key.equals("ts")) ts = value;
                    if (key.equals("v1")) receivedHash = value;
                }
            }

            String manifest = "id:" + dataId + ";request-id:" + requestId + ";ts:" + ts + ";";

            System.out.println("DEBUG MANIFEST: [" + manifest + "]");

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(keySpec);

            byte[] hashBytes = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));
            String calculated = HexFormat.of().formatHex(hashBytes);

            System.out.println("Calculado: " + calculated);
            System.out.println("Recibido:  " + receivedHash);

            return calculated.equalsIgnoreCase(receivedHash);

        } catch (Exception e) {
            System.err.println("Error en validación de firma: " + e.getMessage());
            return false;
        }
    }
}
