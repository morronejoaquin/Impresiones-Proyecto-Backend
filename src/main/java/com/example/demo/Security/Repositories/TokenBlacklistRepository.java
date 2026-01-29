package com.example.demo.Security.Repositories;

import com.example.demo.Security.Model.Entities.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, Long> {

    Optional<TokenBlacklistEntity> findByToken(String token);

    boolean existsByToken(String token);

    void deleteByExpiresAtBefore(LocalDateTime date);
}
