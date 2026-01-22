package com.example.demo.Repositories;

import com.example.demo.Model.Entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {

    Optional<OrderItemEntity> findByIdAndDeletedFalse(UUID id);

    Optional<OrderItemEntity> findByIdAndCartIdAndDeletedFalse(UUID id, UUID cartId);

    List<OrderItemEntity> findAllByCartIdAndDeletedFalse(UUID cartId);
}
