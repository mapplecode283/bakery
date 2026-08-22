package com.mooshi.event;

import java.time.Instant;
import java.util.UUID;

public record DeliveryCompletedEvent(
    UUID eventId,
    String deliveryId,
    String orderId,
    String driverId,
    Instant timestamp
) {
    public static final String TOPIC = "delivery.completed";

    public DeliveryCompletedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
