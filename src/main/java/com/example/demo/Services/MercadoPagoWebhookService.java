package com.example.demo.Services;

import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import com.example.demo.Repositories.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MercadoPagoWebhookService {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public void process(String payload) {

        try {
            JsonNode root = objectMapper.readTree(payload);

            String type = root.path("type").asText();
            Long paymentId = root.path("data").path("id").asLong();

            if (!"payment".equals(type)) return;

            MercadoPagoConfig.setAccessToken(accessToken);

            Payment mpPayment = new PaymentClient().get(paymentId);

            UUID paymentEntityId =
                    UUID.fromString(mpPayment.getExternalReference());

            PaymentEntity paymentEntity =
                    paymentRepository.findById(paymentEntityId)
                            .orElseThrow();

            paymentEntity.setMpPaymentId(mpPayment.getId());

            switch (mpPayment.getStatus()) {
                case "approved" -> {
                    paymentEntity.setPaymentStatus(PaymentStatusEnum.APPROVED);
                    paymentEntity.setPaidAt(Instant.now());
                }
                case "rejected" ->
                        paymentEntity.setPaymentStatus(PaymentStatusEnum.REJECTED);
                case "pending" ->
                        paymentEntity.setPaymentStatus(PaymentStatusEnum.PENDING);
            }

            paymentRepository.save(paymentEntity);

        } catch (MPApiException e) {
            System.err.println("MP API error: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
