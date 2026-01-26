package com.example.demo.Security.Repositories;

import com.example.demo.Security.Model.Entities.CredentialsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<CredentialsEntity, Long> {
    Optional<CredentialsEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
}
