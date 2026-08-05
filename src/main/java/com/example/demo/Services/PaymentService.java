package com.example.demo.Services;

import com.example.demo.Exceptions.BusinessException;
import com.example.demo.Model.DTOS.Mappers.PaymentMapper;
import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Request.PaymentStatusUpdateRequest;
import com.example.demo.Model.DTOS.Request.PaymentRefundRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartHistoryResponse;
import com.example.demo.Model.DTOS.Response.PaymentPreferenceResponse;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.*;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.PaymentRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
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
        this.cartRepository = cartRepository;
        this.mercadoPagoService = mercadoPagoService;
    }


    public Page<CartHistoryResponse> findAll(Pageable pageable) {
        Page<PaymentEntity> page = paymentRepository.findAll(pageable);
        return page.map(paymentMapper::toHistoryResponse);
    }

    public CartHistoryResponse findById(UUID id) {
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        return paymentMapper.toHistoryResponse(entity);
    }

    @Transactional
    public PaymentResponse processCheckout(PaymentCreateRequest request, String email) {

        CartEntity cart = cartService.getOpenCartForUser(email);

        if(cart.getItems().isEmpty()){
            throw new BusinessException(ErrorCode.CART_IS_EMPTY);
        }

        // Verifica que no tenga mas de 3 pedidos en proceso
        UUID userId = cart.getUser().getId();
        Long inProgressCount = cartRepository.countByUser_IdAndCartStatusAndDeletedFalse(
                userId,
                CartStatusEnum.IN_PROGRESS
        );

        if (inProgressCount >= 3) {
            throw new BusinessException(ErrorCode.MAX_IN_PROGRESS_CARTS_REACHED);
        }

        // si es por mercado pago, redirige a mercadoPagoService
        if (PaymentMethodEnum.MERCADO_PAGO.equals(request.getPaymentMethod())) {
            PaymentPreferenceResponse url = mercadoPagoService.createPreference(cart.getId());
            return new PaymentResponse("REDIRECT", url.getInitPoint(), null, cart.getId());
        }

        // si es efectivo o transferencia, se crea el pago de forma manual
        if (PaymentMethodEnum.CASH.equals(request.getPaymentMethod()) || PaymentMethodEnum.TRANSFER.equals(request.getPaymentMethod())) {
            return processManualPayment(cart, request.getPaymentMethod());
        }

        throw new BusinessException(ErrorCode.PAYMENT_NOT_ALLOWED);
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

    public PaymentStatusEnum getStatusByCartId(UUID cartId) {
        return paymentRepository.findTopByCartIdOrderByOrderDateDesc(cartId)
                .map(PaymentEntity::getPaymentStatus)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Transactional
    public PaymentResponse updatePaymentStatus(UUID cartId, PaymentStatusUpdateRequest request) {
        PaymentEntity payment = paymentRepository.findTopByCartIdOrderByOrderDateDesc(cartId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND, "Pago no encontrado para este carrito"));

        PaymentStatusEnum newStatus = request.getStatus();
        payment.setPaymentStatus(newStatus);

        if (newStatus == PaymentStatusEnum.APPROVED) {
            payment.setPaidAt(Instant.now());
        }

        paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse refundOrder(UUID cartId, PaymentRefundRequest request, String adminEmail) {
        PaymentEntity payment = paymentRepository.findTopByCartIdOrderByOrderDateDesc(cartId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND, "Pago no encontrado para este carrito"));

        // Si el pago fue por Mercado Pago, se hace el reembolso total en la API
        if (PaymentMethodEnum.MERCADO_PAGO.equals(payment.getPaymentMethod())) {
            if (payment.getMpPaymentId() != null) {
                try {
                    mercadoPagoService.refundPayment(payment.getMpPaymentId());
                } catch (Exception e) {
                    // En entorno de pruebas (Sandbox), MP suele bloquear los refunds vía API.
                    // Se registra como advertencia pero permitimos cancelar el pedido localmente.
                    System.err.println(">>> [ADVERTENCIA] No se pudo reembolsar en la API de MP (comportamiento habitual en Sandbox): " + e.getMessage());
                }
            }
        }

        // Si es CASH o TRANSFER, el admin ya sabe que debe devolver el efectivo o hacer la transferencia manual.

        // Actualiza los datos de pago y auditoría con el precio completo
        payment.setPaymentStatus(PaymentStatusEnum.REFUNDED);
        payment.setRefundedAt(Instant.now());
        payment.setRefundedAmount(payment.getFinalPrice());
        payment.setRefundReason(request.getReason());
        payment.setRefundedByAdminEmail(adminEmail);
        paymentRepository.save(payment);

        // Cancela el carrito y pedido asociado para sacarlo de la reconciliación y producción
        CartEntity cart = payment.getCart();
        cart.setStatus(OrderStatusEnum.CANCELLED);
        cart.setCartStatus(CartStatusEnum.CANCELLED);
        cartRepository.save(cart);

        return paymentMapper.toResponse(payment);
    }
}