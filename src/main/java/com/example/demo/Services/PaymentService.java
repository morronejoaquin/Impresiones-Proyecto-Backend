package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.PaymentMapper;
import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Response.PaymentResponse;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentMapper paymentMapper, PaymentRepository paymentRepository) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
    }

    public void save(PaymentCreateRequest request){
        PaymentEntity entity = paymentMapper.toEntity(request);
        paymentRepository.save(entity);
    }

    public Page<PaymentResponse> findAll(Pageable pageable){
        Page<PaymentEntity> page = paymentRepository.findAll(pageable);
        return page.map(paymentMapper::toResponse);
    }

    public PaymentResponse findById(UUID id){
        PaymentEntity entity = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado"));

        return paymentMapper.toResponse(entity);
    }
}
