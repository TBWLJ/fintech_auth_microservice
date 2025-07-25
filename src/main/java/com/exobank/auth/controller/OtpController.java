package com.exobank.auth.controller;

import com.exobank.auth.service.OtpService;
import com.exobank.auth.dto.VerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        otpService.generateOtp(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyRequest request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getCode());
        if (valid) return ResponseEntity.ok("OTP verified");
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP");
    }
}
