package com.example.demo.Security.Repositories;

import com.example.demo.Security.Model.Entities.PermitEntity;
import com.example.demo.Security.Model.Enums.Permits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermitRepository extends JpaRepository<PermitEntity, Long> {
    Optional<PermitEntity> findByPermit(Permits permit);

    boolean existsByPermit(Permits permit);
}
