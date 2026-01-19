package com.example.demo.Repositories;

import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findByUser_Id(UUID userId);

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
}
