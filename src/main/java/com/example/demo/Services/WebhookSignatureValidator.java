package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;

@Service
public class WebhookSignatureValidator {

    @Value("${mercadopago.webhook-secret}")
    private String webhookSecret;

    public boolean isValid(String signature, String requestId, String payload) {

        try {
            String signedPayload = requestId + payload;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key =
                    new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");

            mac.init(key);

            byte[] rawHmac = mac.doFinal(signedPayload.getBytes());

            String calculatedSignature =
                    HexFormat.of().formatHex(rawHmac);

            return calculatedSignature.equals(signature);

        } catch (Exception e) {
            return false;
        }
    }
}
