package com.example.demo.Security.Services;

import com.example.demo.Security.Model.Entities.CredentialsEntity;
import com.example.demo.Security.Model.Entities.RefreshToken;
import com.example.demo.Security.Repositories.CredentialsRepository;
import com.example.demo.Security.Repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final CredentialsRepository credentialsRepository;

    @Transactional
    public RefreshToken createRefreshToken(String email) {
        CredentialsEntity user = credentialsRepository.findByEmail(email).orElseThrow();
        // Borramos tokens antiguos del usuario para evitar acumulación
        refreshTokenRepository.deleteByUser(user);

        refreshTokenRepository.flush();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusMillis(604800000)) // 7 días
                .token(UUID.randomUUID().toString())
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado");
        }
        return token;
    }
}
