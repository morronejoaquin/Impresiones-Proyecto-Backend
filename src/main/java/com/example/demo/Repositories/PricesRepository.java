package com.example.demo.Repositories;

import com.example.demo.Model.Entities.PricesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PricesRepository extends JpaRepository<PricesEntity, UUID> {
}
