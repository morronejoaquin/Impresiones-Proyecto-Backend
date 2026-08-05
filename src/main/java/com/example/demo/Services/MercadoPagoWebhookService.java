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

            // Extraer ID si viene en data.id
            if ((finalDataId == null || finalDataId.trim().isEmpty()) && root.has("data")) {
                finalDataId = root.path("data").path("id").asText();
            }
            // Extraer ID si viene en resource (IPN clásico)
            if ((finalDataId == null || finalDataId.trim().isEmpty()) && root.has("resource")) {
                String resource = root.path("resource").asText();
                if (resource.contains("/")) {
                    String[] segments = resource.split("/");
                    finalDataId = segments[segments.length - 1];
                }
            }

            if (finalDataId == null || finalDataId.trim().isEmpty()) {
                System.err.println(">>> ERROR: No se pudo encontrar el ID del recurso en el webhook.");
                return;
            }

            // Validar firma
            // Para la validacion se requiere una URL publica estable y un dominio de produccion
            boolean isSignatureValid = signatureValidator.isValid(signature, requestId, finalDataId);
            if (!isSignatureValid) {
                // Logueamos para debug, pero NO detenemos el proceso
                System.err.println(">>> [ADVERTENCIA] La firma del webhook no coincide. ID: " + finalDataId);
            }

            // Manejar tipos de eventos de Mercado Pago
            String type = root.path("type").asText();
            String action = root.path("action").asText();

            // Aceptamos tanto eventos de pago directos como creaciones/actualizaciones de merchant_order
            boolean isProcessable = "payment".equals(type) ||
                    action.startsWith("payment") ||
                    root.has("resource") ||
                    "topic_merchant_order_wh".equals(type);

            if (!isProcessable) {
                System.out.println("Evento ignorado (tipo: " + type + ", action: " + action + ")");
                return;
            }

            // Si el webhook es de tipo merchant_order, necesitamos buscar el pago asociado dentro de esa orden
            Long paymentId;
            if ("topic_merchant_order_wh".equals(type) || root.has("resource") && root.path("resource").asText().contains("merchant_orders")) {
                // Consultamos la merchant order en MP para sacar el id del pago real aprobado
                MercadoPagoConfig.setAccessToken(accessToken);
                com.mercadopago.client.merchantorder.MerchantOrderClient orderClient = new com.mercadopago.client.merchantorder.MerchantOrderClient();
                com.mercadopago.resources.merchantorder.MerchantOrder order = orderClient.get(Long.parseLong(finalDataId));

                if (order.getPayments() == null || order.getPayments().isEmpty()) {
                    System.out.println("Merchant order sin pagos todavía.");
                    return;
                }
                // Tomamos el ID del pago aprobado o el último pago de la orden
                paymentId = order.getPayments().get(order.getPayments().size() - 1).getId();
            } else {
                paymentId = Long.parseLong(finalDataId);
            }

            MercadoPagoConfig.setAccessToken(accessToken);
            Payment mpPayment = new PaymentClient().get(paymentId);

            if (mpPayment.getExternalReference() == null) {
                System.err.println(">>> ERROR: El pago de MercadoPago no tiene externalReference.");
                return;
            }

            UUID paymentEntityId = UUID.fromString(mpPayment.getExternalReference());
            PaymentEntity paymentEntity = paymentRepository.findById(paymentEntityId).orElseThrow();

            paymentEntity.setMpPaymentId(mpPayment.getId());

            if (paymentEntity.getPaymentStatus() == PaymentStatusEnum.APPROVED) {
                return; // Ya procesado
            }

            if ("approved".equals(mpPayment.getStatus())) {
                paymentEntity.setPaymentStatus(PaymentStatusEnum.APPROVED);
                paymentEntity.setPaidAt(Instant.now());

                CartEntity cart = paymentEntity.getCart();
                cart.setCartStatus(CartStatusEnum.IN_PROGRESS);
                cart.setStatus(OrderStatusEnum.PENDING);
                cart.setAdmReceivedAt(Instant.now());

                for (OrderItemEntity item : cart.getItems()) {
                    item.setPricePerSheet(pricingService.obtenerPreciosVigentes().getPricePerSheetBW());
                    item.setBindingPrice(pricingService.obtenerPreciosVigentes().getPriceRingedBinding());
                    orderItemRepository.save(item);
                }

                cartRepository.save(cart);
            } else if ("rejected".equals(mpPayment.getStatus())) {
                paymentEntity.setPaymentStatus(PaymentStatusEnum.REJECTED);
            } else if ("pending".equals(mpPayment.getStatus())) {
                paymentEntity.setPaymentStatus(PaymentStatusEnum.PENDING);
            }

            paymentRepository.save(paymentEntity);
            System.out.println(">>> [EXITO] Pago procesado correctamente para el carrito ID: " + paymentEntity.getCart().getId());

        } catch (Exception e) {
            System.err.println(">>> ERROR EN PROCESAMIENTO DE WEBHOOK: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
