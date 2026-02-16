package com.example.demo.Services;

import com.example.demo.Model.DTOS.Response.PaymentPreferenceResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.PaymentMethodEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class MercadoPagoService {

    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @Value("${app.webhook-url}")
    private String webhookUrl;

    public MercadoPagoService(CartRepository cartRepository, PaymentRepository paymentRepository) {
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
    }

    public PaymentPreferenceResponse createPreference(UUID cartId) {

        MercadoPagoConfig.setAccessToken(accessToken);

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        // 1. BUSCAR si ya existe un pago PENDING para este carrito
        PaymentEntity payment = paymentRepository.findByCartAndPaymentStatus(cart, PaymentStatusEnum.PENDING)
                .orElseGet(() -> {
                    // Si no existe, CREAMOS uno nuevo
                    PaymentEntity newPayment = new PaymentEntity();
                    newPayment.setCart(cart);
                    newPayment.setFinalPrice(cart.getTotal());
                    newPayment.setPaymentMethod(PaymentMethodEnum.MERCADO_PAGO);
                    newPayment.setPaymentStatus(PaymentStatusEnum.PENDING);
                    newPayment.setOrderDate(Instant.now());
                    newPayment.setDepositAmount(0); // Para evitar error de null en la DB
                    return paymentRepository.save(newPayment);
                });

        payment.setFinalPrice(cart.getTotal());
        payment = paymentRepository.save(payment);

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title("Orden de impresión #" + cart.getId())
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(cart.getTotal()))
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("http://localhost:4200/order-received")
                .pending("http://localhost:4200/order-received")
                .failure("http://localhost:4200/cart-payment")
                .build();

        System.out.println(backUrls.getSuccess());
        System.out.println(backUrls.getPending());
        System.out.println(backUrls.getFailure());

        PreferenceRequest request =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .externalReference(payment.getId().toString())
                        .notificationUrl(webhookUrl)
                        .backUrls(backUrls)
                        // .autoReturn("approved") esto se desactiva hasta que tengamos un servidor real
                        .build();

        try {
            Preference preference = new PreferenceClient().create(request);

            payment.setMpPreferenceId(preference.getId());
            paymentRepository.save(payment);

            return new PaymentPreferenceResponse(preference.getInitPoint());

        } catch (MPApiException e) {
            throw new RuntimeException("Error MercadoPago API: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            throw new RuntimeException("Error MercadoPago SDK", e);
        }
    }
}
