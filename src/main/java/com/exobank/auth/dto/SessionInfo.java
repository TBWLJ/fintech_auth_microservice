package com.exobank.auth.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class SessionInfo {
    private Long id;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;
    private Instant expiryDate;
}
