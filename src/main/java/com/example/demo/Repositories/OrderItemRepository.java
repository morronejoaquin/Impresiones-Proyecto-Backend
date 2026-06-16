package com.example.demo.Repositories;

import com.example.demo.Model.DTOS.Response.PrintingStatisticsQueryResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {

    Optional<OrderItemEntity> findByIdAndDeletedFalse(UUID id);

    Optional<OrderItemEntity> findByIdAndCartIdAndDeletedFalse(UUID id, UUID cartId);

    List<OrderItemEntity> findAllByCartIdAndDeletedFalse(UUID cartId);

    List<OrderItemEntity> findAllByDeletedTrue();

    @Query("SELECT new com.example.demo.Model.DTOS.Response.PrintingStatisticsQueryResponse(" +
            "SUM(o.pages * o.copies), " +
            "SUM(CASE WHEN o.color = true THEN o.pages * o.copies ELSE 0 END), " +
            "SUM(CASE WHEN o.color = false THEN o.pages * o.copies ELSE 0 END), " +
            "SUM(CASE WHEN o.binding = 'RINGED' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN o.binding = 'STAPLED' THEN 1 ELSE 0 END), " +
            "COUNT(o)) " +
            "FROM OrderItemEntity o " +
            "WHERE o.cart.lastModifiedAt BETWEEN :start AND :end " +
            "AND o.deleted = false " +
            "AND o.cart.status != 'PENDING'" +
            "AND o.cart.cartStatus != 'OPEN'" +
            "AND o.cart.cartStatus != 'CANCELLED'")
    PrintingStatisticsQueryResponse getPrintingStats(@Param("start") Instant start, @Param("end") Instant end);
}
