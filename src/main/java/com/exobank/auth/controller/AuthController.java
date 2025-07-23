package com.exobank.auth.controller;

import com.exobank.auth.dto.LoginRequest;
import com.exobank.auth.dto.RegisterRequest;
import com.exobank.auth.entity.User;
import com.exobank.auth.repository.UserRepository;
import com.exobank.auth.service.OtpService;
import com.exobank.auth.utils.AccountNumberGenerator;
import com.exobank.auth.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        if (!user.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Please verify your email or phone number using the OTP sent.");
        }

        String token = jwtUtils.generateToken(
            user.getId().toString(),
            user.getEmail(),
            user.isAdmin()
        );

        return ResponseEntity.ok(Map.of("token", token));
    }
}
