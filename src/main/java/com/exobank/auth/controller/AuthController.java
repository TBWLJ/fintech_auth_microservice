package com.exobank.auth.controller;

import com.exobank.auth.dto.LoginRequest;
import com.exobank.auth.dto.RegisterRequest;
import com.exobank.auth.dto.SessionInfo;
import com.exobank.auth.entity.RefreshToken;
import com.exobank.auth.entity.User;
import com.exobank.auth.repository.UserRepository;
import com.exobank.auth.service.OtpService;
import com.exobank.auth.utils.AccountNumberGenerator;
import com.exobank.auth.utils.JwtUtils;
import com.exobank.auth.repository.RefreshTokenRepository;
import com.exobank.auth.service.RefreshTokenService;

import jakarta.servlet.http.HttpServletRequest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNumberGenerator accountNumberGenerator;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use.");
        }

        User user = new User();
        user.setFirstName(req.getFirstname());
        user.setLastName(req.getLastname());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setAccountNumber(accountNumberGenerator.generate());
        user.setAdmin(false);

        userRepository.save(user);

        // Send OTP
        otpService.generateOtp(user.getEmail());

        return ResponseEntity.ok("User registered successfully. OTP sent to your email.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        if (!user.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Please verify your email or phone number using the OTP sent.");
        }

        String accessToken = jwtUtils.generateToken(user.getId().toString(), user.getEmail(), user.isAdmin());
        String refreshToken = refreshTokenService.createRefreshToken(user, servletRequest).getToken();

        return ResponseEntity.ok(Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        try {
            RefreshToken rt = refreshTokenService.verify(refreshToken);

            User user = rt.getUser();
            String newAccess = jwtUtils.generateToken(
                    user.getId().toString(), user.getEmail(), user.isAdmin());

            // 🌟 Rotate refresh token (optional but recommended)
            refreshTokenService.revoke(rt);
            String newRefresh = refreshTokenService.createRefreshToken(user, null).getToken();

            return ResponseEntity.ok(
                Map.of("accessToken", newAccess, "refreshToken", newRefresh)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String refreshToken) {
        refreshTokenService.verify(refreshToken);   // throws if invalid
        refreshTokenService.revoke(
                refreshTokenService.verify(refreshToken)
        );
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> getSessions(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        List<SessionInfo> sessions = refreshTokenRepository
            .findByUser_Id(Long.valueOf(userId))
            .stream()
            .map(token -> {
                SessionInfo info = new SessionInfo();
                info.setId(token.getId());
                info.setIpAddress(token.getIpAddress());
                info.setUserAgent(token.getUserAgent());
                info.setCreatedAt(token.getCreatedAt());
                info.setExpiryDate(token.getExpiryDate());
                return info;
            })
            .toList();

        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/logout/all-sessions")
    public ResponseEntity<?> logoutAll(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        refreshTokenRepository.deleteByUser_Id(Long.valueOf(userId));
        return ResponseEntity.ok("Logged out from all devices.");
    }
}
