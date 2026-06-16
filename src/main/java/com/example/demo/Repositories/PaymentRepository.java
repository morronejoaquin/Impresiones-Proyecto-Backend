package com.example.demo.Repositories;

import com.example.demo.Model.DTOS.Response.PaymentSummaryByMethodQueryResponse;
import com.example.demo.Model.DTOS.Response.PaymentSummaryByMethodResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByCartAndPaymentStatus(CartEntity cart, PaymentStatusEnum status);

    Optional<PaymentEntity> findTopByCartIdOrderByOrderDateDesc(UUID cartId);

    @Query("SELECT new com.example.demo.Model.DTOS.Response.PaymentSummaryByMethodQueryResponse(" +
            "p.paymentMethod, SUM(p.finalPrice), COUNT(p)) " +
            "FROM PaymentEntity p " +
            "WHERE p.paidAt BETWEEN :start AND :end " +
            "AND p.paymentStatus = 'APPROVED' " +
            "GROUP BY p.paymentMethod")
    List<PaymentSummaryByMethodQueryResponse> getPaymentSummary(@Param("start") Instant start, @Param("end") Instant end);
}
