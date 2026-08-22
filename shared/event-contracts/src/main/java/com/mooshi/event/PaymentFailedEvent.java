package com.mooshi.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
    UUID eventId,
    String orderId,
    String customerId,
    BigDecimal amount,
    String reason,
    Instant timestamp
) {
    public static final String TOPIC = "payment.failed";

    public PaymentFailedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
