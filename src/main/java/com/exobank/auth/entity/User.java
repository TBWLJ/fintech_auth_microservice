package com.exobank.auth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// entity/User.java
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;

    @Column(unique = true)
    private String accountNumber;

    private String bvn;
    private String nin;
    private boolean verified = false;
    private String role = "USER";
    private LocalDateTime createdAt = LocalDateTime.now();
}
