package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.PaymentMapper;
import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;

    @Autowired
    public PaymentService(PaymentMapper paymentMapper,
                          PaymentRepository paymentRepository,
                          CartRepository cartRepository) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
    }

    public PaymentResponse save(PaymentCreateRequest request) {
        CartEntity cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        if (cart.getCartStatus() != CartStatusEnum.OPEN) {
            throw new IllegalStateException("El carrito no está abierto. No se puede realizar el pago.");
        }

        PaymentEntity payment = paymentMapper.toEntity(request);
        payment.setCart(cart);
        payment.setFinalPrice(cart.getTotal());
        payment.setOrderDate(Instant.now());
        payment.setPaymentStatus(PaymentStatusEnum.PENDING);

        paymentRepository.save(payment);

        cart.setCartStatus(CartStatusEnum.PAID);
        cartRepository.save(cart);

        return paymentMapper.toResponse(payment);
    }

    public Page<PaymentResponse> findAll(Pageable pageable) {
        Page<PaymentEntity> page = paymentRepository.findAll(pageable);
        return page.map(paymentMapper::toResponse);
    }

    public PaymentResponse findById(UUID id) {
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado"));

        return paymentMapper.toResponse(entity);
    }
}