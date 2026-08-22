package com.mooshi.auth.dto;

import java.time.Instant;

public record UserResponse(
    String id,
    String email,
    String firstName,
    String lastName,
    boolean enabled,
    Instant createdAt
) {}
