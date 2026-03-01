package com.example.demo.Services;

import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.OrderStatusEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.OrderItemRepository;
import com.example.demo.Repositories.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import jakarta.transaction.Transactional;
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
    private final CartRepository cartRepository;
    private final WebhookSignatureValidator signatureValidator;
    private final PricingService pricingService;
    private final OrderItemRepository orderItemRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Transactional
    public void process(String payload, String signature, String requestId, String dataIdUrl) {

        try {
            JsonNode root = objectMapper.readTree(payload);

            String finalDataId = dataIdUrl;
            if (finalDataId == null && root.has("data")) {
                finalDataId = root.path("data").path("id").asText();
            }

            if (finalDataId == null || finalDataId.trim().isEmpty()) {
                System.err.println(">>> ERROR: No se pudo encontrar el ID del recurso en el webhook.");
                return;
            }

            boolean isSignatureValid = signatureValidator.isValid(signature, requestId, finalDataId);

            if (!isSignatureValid) {
                // Logueamos para debug, pero NO detenemos el proceso con un return
                System.err.println(">>> [LOG] Firma aún no coincide. ID: " + finalDataId);
            } else {
                System.out.println(">>> [OK] ¡FIRMA VALIDADA! Origen verificado.");
            }

            // 3. Lógica de negocio
            String type = root.path("type").asText();
            // Solo procesamos si el evento es de tipo payment
            if (!"payment".equals(type)) {
                System.out.println("Evento ignorado (tipo: " + type + ")");
                return;
            }

            // Convertir el ID seguro
            Long paymentId = Long.parseLong(finalDataId);
            MercadoPagoConfig.setAccessToken(accessToken);

            Payment mpPayment = new PaymentClient().get(paymentId);

            UUID paymentEntityId =
                    UUID.fromString(mpPayment.getExternalReference());

            PaymentEntity paymentEntity =
                    paymentRepository.findById(paymentEntityId)
                            .orElseThrow();

            paymentEntity.setMpPaymentId(mpPayment.getId());

            if (paymentEntity.getPaymentStatus() == PaymentStatusEnum.APPROVED) {
                return;  // en caso de que el webhook llegue dos veces
            }

            switch (mpPayment.getStatus()) {
                case "approved" -> {
                    paymentEntity.setPaymentStatus(PaymentStatusEnum.APPROVED);
                    paymentEntity.setPaidAt(Instant.now());

                    CartEntity cart = paymentEntity.getCart();
                    cart.setCartStatus(CartStatusEnum.IN_PROGRESS);
                    cart.setStatus(OrderStatusEnum.PENDING);
                    cart.setAdmReceivedAt(Instant.now());

                    for (OrderItemEntity item : cart.getItems()){
                        item.setPricePerSheet(pricingService.obtenerPreciosVigentes().getPricePerSheetBW());
                        item.setBindingPrice(pricingService.obtenerPreciosVigentes().getPriceRingedBinding());
                        orderItemRepository.save(item);
                    }

                    cartRepository.save(cart);

                }
                case "rejected" ->
                        paymentEntity.setPaymentStatus(PaymentStatusEnum.REJECTED);
                case "pending" ->
                        paymentEntity.setPaymentStatus(PaymentStatusEnum.PENDING);
            }

            paymentRepository.save(paymentEntity);

        } catch (Exception e) {
            System.err.println(">>> ERROR EN PROCESAMIENTO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
