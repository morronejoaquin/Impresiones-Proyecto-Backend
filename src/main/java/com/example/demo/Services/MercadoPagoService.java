package com.example.demo.Services;

import com.example.demo.Model.DTOS.Response.PaymentPreferenceResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.PaymentMethodEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.PaymentRepository;
import com.mercadopago.MercadoPagoConfig;
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

        PaymentEntity payment = new PaymentEntity();
        payment.setCart(cart);
        payment.setFinalPrice(cart.getTotal());
        payment.setPaymentMethod(PaymentMethodEnum.MERCADO_PAGO);
        payment.setPaymentStatus(PaymentStatusEnum.PENDING);
        payment.setOrderDate(Instant.now());

        payment = paymentRepository.save(payment);

        PreferenceItemRequest item =
                PreferenceItemRequest.builder()
                        .title("Orden de impresión")
                        .quantity(1)
                        .unitPrice(BigDecimal.valueOf(cart.getTotal()))
                        .build();

        PreferenceRequest request =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .externalReference(payment.getId().toString())
                        .notificationUrl(webhookUrl)
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
