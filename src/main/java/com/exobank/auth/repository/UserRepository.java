package com.exobank.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.exobank.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
