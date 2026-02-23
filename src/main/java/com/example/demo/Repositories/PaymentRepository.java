package com.example.demo.Repositories;

import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByCartAndPaymentStatus(CartEntity cart, PaymentStatusEnum status);

    Optional<PaymentEntity> findTopByCartIdOrderByOrderDateDesc(UUID cartId);

    List<PaymentEntity> findAllByPaidAtBetween(Instant start, Instant end);
}
