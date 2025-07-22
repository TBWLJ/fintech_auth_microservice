package com.exobank.auth.utils;

import com.exobank.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final UserRepository userRepository;

    public String generate() {
        String accountNumber;
        do {
            accountNumber = "3" + String.format("%09d", new Random().nextInt(1_000_000_000));
        } while (userRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
