package com.exobank.auth.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long EXPIRATION_TIME = 86400000; // 1 day

    public String generateToken(String userId, String email, boolean isAdmin) {
        return JWT.create()
                .withSubject(userId)
                .withClaim("email", email)
                .withClaim("isAdmin", isAdmin)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String validateAndExtractUserId(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
                .build()
                .verify(token)
                .getSubject();
    }
}
