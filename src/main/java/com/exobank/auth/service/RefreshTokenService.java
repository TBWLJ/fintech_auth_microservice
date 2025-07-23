package com.exobank.auth.service;

import com.exobank.auth.entity.RefreshToken;
import com.exobank.auth.entity.User;
import com.exobank.auth.repository.RefreshTokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

// import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    // 7‑day validity
    // private static final long REFRESH_EXP_DAYS = 7;

    public RefreshToken createRefreshToken(User user, HttpServletRequest request) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));

        token.setIpAddress(request.getRemoteAddr());
        token.setUserAgent(request.getHeader("User-Agent"));

        return repo.save(token);
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
