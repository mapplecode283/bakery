package com.mooshi.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderPaidEvent(
    UUID eventId,
    String orderId,
    String customerId,
    String paymentId,
    BigDecimal amount,
    Instant timestamp
) {
    public static final String TOPIC = "order.paid";

    public OrderPaidEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
