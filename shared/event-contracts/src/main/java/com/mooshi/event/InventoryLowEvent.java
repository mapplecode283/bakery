package com.mooshi.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InventoryLowEvent(
    UUID eventId,
    String ingredientId,
    String ingredientName,
    BigDecimal currentStock,
    BigDecimal threshold,
    Instant timestamp
) {
    public static final String TOPIC = "inventory.low";

    public InventoryLowEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
