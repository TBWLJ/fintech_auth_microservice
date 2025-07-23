package com.exobank.auth.service;

import com.exobank.auth.entity.RefreshToken;
import com.exobank.auth.entity.User;
import com.exobank.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    // 7‑day validity
    private static final long REFRESH_EXP_DAYS = 7;

    public RefreshToken create(User user) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUser(user);
        rt.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_EXP_DAYS));
        return repo.save(rt);
    }

    public RefreshToken verify(String token) {
        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (rt.isRevoked() || rt.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Expired or revoked refresh token");

        return rt;
    }

    public void revoke(RefreshToken rt) {
        rt.setRevoked(true);
        repo.save(rt);
    }
}
