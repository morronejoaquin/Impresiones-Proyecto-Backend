package com.example.demo.Security.Repositories;

import com.example.demo.Security.Model.Entities.RoleEntity;
import com.example.demo.Security.Model.Enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(Rol role);
}
