package com.mooshi.inventory.event;

import com.mooshi.event.InventoryLowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryLow(InventoryLowEvent event) {
        kafkaTemplate.send(InventoryLowEvent.TOPIC, event.ingredientId(), event);
    }
}
