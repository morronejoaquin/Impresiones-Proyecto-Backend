package com.example.demo.Repositories;

import com.example.demo.Model.Entities.CartEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findByUser(UUID id);

    Page<CartEntity> findByStatusOrderByCompletedAtDesc(
        com.example.demo.Model.Enums.OrderStatusEnum status,
        Pageable pageable
    );
}
