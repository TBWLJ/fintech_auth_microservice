package com.exobank.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.exobank.auth.entity.Otp;
import com.exobank.auth.repository.OtpRepository;
import com.exobank.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;

    public String generateOtp(String email) {
        // Check if OTP was sent recently
        Optional<Otp> existingOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (existingOtp.isPresent()) {
            Otp lastOtp = existingOtp.get();
            if (lastOtp.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(60))) {
                throw new RuntimeException("Please wait at least 1 minute before requesting another OTP");
            }
            else {
                System.out.println("Last OTP was created at: " + lastOtp.getCreatedAt());
            }
        }

        String otpCode = String.format("%06d", new Random().nextInt(999999));

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otp.setCreatedAt(LocalDateTime.now());

        otpRepository.save(otp);

        System.out.println("OTP for " + email + " is " + otpCode);

        return otpCode;
    }

    public boolean verifyOtp(String email, String code) {
        System.out.println("Verifying OTP for email: " + email + " and code: " + code);

        Optional<Otp> latestOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (latestOtp.isEmpty()) {
            System.out.println("No OTP found for email");
            return false;
        }

        Otp otp = latestOtp.get();
        System.out.println("Found OTP in DB: " + otp.getCode());

        if (!otp.getCode().equals(code)) {
            System.out.println("OTP code mismatch");
            return false;
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            System.out.println("OTP expired");
            return false;
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setVerified(true);
            userRepository.save(user);
            System.out.println("User marked as verified");
        });

        return true;
    }
}
