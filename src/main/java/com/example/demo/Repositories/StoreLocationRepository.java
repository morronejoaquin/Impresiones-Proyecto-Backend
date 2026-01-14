package com.example.demo.Repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Entities.StoreLocationEntity;

public interface StoreLocationRepository extends JpaRepository<StoreLocationEntity, UUID> {}
