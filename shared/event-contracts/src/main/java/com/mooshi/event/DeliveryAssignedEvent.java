package com.mooshi.event;

import java.time.Instant;
import java.util.UUID;

public record DeliveryAssignedEvent(
    UUID eventId,
    String deliveryId,
    String orderId,
    String driverId,
    Instant timestamp
) {
    public static final String TOPIC = "delivery.assigned";

    public DeliveryAssignedEvent {
        if (eventId == null) eventId = UUID.randomUUID();
        if (timestamp == null) timestamp = Instant.now();
    }
}
