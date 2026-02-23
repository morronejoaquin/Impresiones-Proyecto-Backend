package com.example.demo.Repositories;

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

    @Query("SELECT o FROM OrderItemEntity o WHERE o.cart.createdAt BETWEEN :start AND :end AND o.deleted = false")
    List<OrderItemEntity> findAllByCreatedAtBetween(@Param("start") Instant start, @Param("end") Instant end);
}
