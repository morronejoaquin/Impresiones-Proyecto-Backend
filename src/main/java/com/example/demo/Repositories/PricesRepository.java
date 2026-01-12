package com.example.demo.Repositories;

import com.example.demo.Model.Entities.PricesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PricesRepository extends JpaRepository<PricesEntity, UUID> {

    Optional<PricesEntity> findFirstByValidFromLessThanEqualAndValidToGreaterThanEqual(
            Instant now1,
            Instant now2
    );
}
