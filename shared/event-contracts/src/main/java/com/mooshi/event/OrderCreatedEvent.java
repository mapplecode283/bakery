package com.mooshi.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID eventId,
    String orderId,
    String customerId,
    List<OrderItem> items,
    BigDecimal totalAmount,
    Instant timestamp
) {
    public static final String TOPIC = "order.created";

    public record OrderItem(String productId, String productName, int quantity, BigDecimal price) {}

    public OrderCreatedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
