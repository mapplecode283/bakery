package com.mooshi.event;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
    UUID eventId,
    String userId,
    String email,
    String firstName,
    String lastName,
    Instant timestamp
) {
    public static final String TOPIC = "user.registered";

    public UserRegisteredEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
