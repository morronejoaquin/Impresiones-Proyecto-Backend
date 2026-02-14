package com.example.demo.Repositories;

import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Enums.AdminDateFilterType;
import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
    List<CartEntity> findByUser_Id(UUID userId);

    Page<CartEntity> findByStatusOrderByAdmReceivedAtAsc(
        com.example.demo.Model.Enums.OrderStatusEnum status,
        Pageable pageable
    );

    @Query("""
    SELECT c FROM CartEntity c
    WHERE (:status IS NULL OR c.status = :status)
      AND (:from IS NULL OR c.admReceivedAt >= :from)
      AND (:to IS NULL OR c.admReceivedAt <= :to)
      AND (:userId IS NULL OR c.user.id = :userId)
      AND c.deleted = false
    ORDER BY c.admReceivedAt DESC
    """)
    Page<CartEntity> findByFilters(
            @Param("status")OrderStatusEnum status,
            @Param("from")Instant from,
            @Param("to")Instant to,
            @Param("userId") UUID userId,
            Pageable pageable
    );

    Optional<CartEntity> findByUser_IdAndCartStatusAndDeletedFalse(
            UUID userId,
            CartStatusEnum cartStatus
    );

    @Query("""
    SELECT c FROM CartEntity c
    WHERE c.status = :status
      AND c.deleted = false
      AND (
            :date IS NULL
            OR (
                :dateType = 'COMPLETED_AT' AND c.completedAt >= :date
            )
            OR (
                :dateType = 'DELIVERED_AT' AND c.deliveredAt >= :date
            )
            OR (
                :dateType = 'ADM_RECEIVED_AT' AND c.admReceivedAt >= :date
            )
      )
    ORDER BY c.admReceivedAt DESC
    """)
    Page<CartEntity> findDeliveredForAdmin(
            @Param("status") OrderStatusEnum status,
            @Param("date") Instant date,
            @Param("dateType") AdminDateFilterType dateType,
            Pageable pageable
    );

    List<CartEntity> findByCartStatusAndLastModifiedAtBefore(CartStatusEnum status, Instant date);

    @Query("""
    SELECT c FROM CartEntity c
    WHERE c.user.id = :userId
      AND c.status IS NOT NULL
      AND c.deleted = false
    ORDER BY c.createdAt DESC
    """)
    Page<CartEntity> findByUser_IdAndStatusNotNullAndDeletedFalseOrderByCreatedAtDesc(
            @Param("userId") UUID userId,
            Pageable pageable
    );

    Page<CartEntity> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<CartEntity> findAllByStatusInAndDeletedFalseOrderByAdmReceivedAtAsc(
            java.util.List<OrderStatusEnum> statuses,
            Pageable pageable
    );

    @Query("""
    SELECT c FROM CartEntity c
    WHERE (
        :status IS NULL OR c.status = CAST(:status AS com.example.demo.Model.Enums.OrderStatusEnum)
    )
    AND (
        :startDate IS NULL OR c.admReceivedAt >= CAST(:startDate AS java.time.Instant)
    )
    AND (
        :endDate IS NULL OR c.admReceivedAt <= CAST(:endDate AS java.time.Instant)
    )
    AND (
        :customerEmail IS NULL OR LOWER(c.customer.email) LIKE LOWER(CONCAT('%', :customerEmail, '%'))
    )
    AND c.status IN (
        com.example.demo.Model.Enums.OrderStatusEnum.PENDING,
        com.example.demo.Model.Enums.OrderStatusEnum.PRINTING,
        com.example.demo.Model.Enums.OrderStatusEnum.BINDING,
        com.example.demo.Model.Enums.OrderStatusEnum.READY
    )
    AND c.deleted = false
    ORDER BY c.admReceivedAt DESC
    """)
    Page<CartEntity> filterCartsForAdmin(
            @Param("status") String status,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("customerEmail") String customerEmail,
            Pageable pageable
    );

    @Query("""
    SELECT c FROM CartEntity c
    WHERE c.status = com.example.demo.Model.Enums.OrderStatusEnum.DELIVERED
    AND (
        :startDate IS NULL OR c.createdAt >= CAST(:startDate AS java.time.Instant)
    )
    AND (
        :endDate IS NULL OR c.deliveredAt <= CAST(:endDate AS java.time.Instant)
    )
    AND c.deleted = false
    ORDER BY c.deliveredAt DESC
    """)
    Page<CartEntity> getDeliveredHistory(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            Pageable pageable
    );

}
