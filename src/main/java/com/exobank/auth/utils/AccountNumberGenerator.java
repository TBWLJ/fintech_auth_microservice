package com.exobank.auth.utils;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.exobank.auth.repository.UserRepository;

@Component
public class AccountNumberGenerator {
    @Autowired private UserRepository userRepository;

    public String next() {
        String acc;
        do {
            acc = String.format("%010d", ThreadLocalRandom.current().nextInt(1_000_000_000));
        } while (userRepository.existsByAccountNumber(acc));
        return acc;
    }
}
