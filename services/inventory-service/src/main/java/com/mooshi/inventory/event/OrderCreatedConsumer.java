package com.mooshi.inventory.event;

import com.mooshi.event.OrderCreatedEvent;
import com.mooshi.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = OrderCreatedEvent.TOPIC, groupId = "inventory-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Processing inventory for orderId={}", event.orderId());
        // Decrement stock for each item in the order
        // Mock: small random deduction per order
        inventoryService.deductForOrder(event);
    }
}
