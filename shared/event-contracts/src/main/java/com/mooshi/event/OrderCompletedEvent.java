package com.mooshi.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCompletedEvent(
    UUID eventId,
    String orderId,
    String customerId,
    String status,
    Instant timestamp
) {
    public static final String TOPIC = "order.completed";

    public OrderCompletedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
