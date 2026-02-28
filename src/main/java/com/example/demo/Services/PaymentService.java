package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.PaymentMapper;
import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Request.PaymentStatusUpdateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartHistoryResponse;
import com.example.demo.Model.DTOS.Response.PaymentPreferenceResponse;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.PaymentMethodEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.PaymentRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;
    private final MercadoPagoService mercadoPagoService;

    @Autowired
    public PaymentService(PaymentMapper paymentMapper,
                          PaymentRepository paymentRepository,
                          CartService cartService,
                          CartRepository cartRepository,
                          MercadoPagoService mercadoPagoService,
                          UserRepository userRepository) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.cartService = cartService;
        this.mercadoPagoService = mercadoPagoService;
    }


    public Page<CartHistoryResponse> findAll(Pageable pageable) {
        Page<PaymentEntity> page = paymentRepository.findAll(pageable);
        return page.map(paymentMapper::toHistoryResponse);
    }

    public CartHistoryResponse findById(UUID id) {
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado"));

        return paymentMapper.toHistoryResponse(entity);
    }

    public PaymentResponse processCheckout(PaymentCreateRequest request, String email) {

        CartEntity cart = cartService.getOpenCartForUser(email);

        if(cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Agregue items antes de procesar el carrito");
        }

        // si es por mercado pago, redirige a mercadoPagoService
        if (PaymentMethodEnum.MERCADO_PAGO.equals(request.getPaymentMethod())) {
            PaymentPreferenceResponse url = mercadoPagoService.createPreference(cart.getId());
            return new PaymentResponse("REDIRECT", url.getInitPoint(), null, cart.getId());
        }

        // si es efectivo, se crea el pago de forma manual
        if (PaymentMethodEnum.CASH.equals(request.getPaymentMethod())) {
            return processManualPayment(cart, request.getPaymentMethod());
        }

        throw new IllegalArgumentException("Método de pago no soportado");
    }

    private PaymentResponse processManualPayment(CartEntity cart, PaymentMethodEnum method) {

        // Actualizar el carrito a un estado que bloquee cambios
        CartResponse cartResponse = cartService.closeCart(cart.getId());

        // Guardar registro del pago pendiente
        PaymentEntity payment = new PaymentEntity();
        payment.setCart(cart);
        payment.setPaymentStatus(PaymentStatusEnum.PENDING);
        payment.setPaymentMethod(method);
        payment.setOrderDate(Instant.now());
        payment.setFinalPrice(cart.getTotal());
        payment.setDepositAmount(0);

        paymentRepository.save(payment);

        return new PaymentResponse("SHOW_INSTRUCTIONS",
                null,
                "Tu pedido ha sido registrado. Por favor, acércate al local para abonar y confirmar la producción.",
                cart.getId());
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(UUID cartId, PaymentStatusUpdateRequest request) {
        PaymentEntity payment = paymentRepository.findTopByCartIdOrderByOrderDateDesc(cartId)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado para este carrito"));

        PaymentStatusEnum newStatus = request.getStatus();
        payment.setPaymentStatus(newStatus);

        if (newStatus == PaymentStatusEnum.APPROVED) {
            payment.setPaidAt(Instant.now());
        }

        paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }
}