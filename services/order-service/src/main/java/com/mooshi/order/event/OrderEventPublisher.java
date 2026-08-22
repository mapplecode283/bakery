package com.mooshi.order.event;

import com.mooshi.event.OrderCompletedEvent;
import com.mooshi.event.OrderCreatedEvent;
import com.mooshi.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(OrderCreatedEvent.TOPIC, event.orderId(), event);
    }

    public void publishOrderPaid(OrderPaidEvent event) {
        kafkaTemplate.send(OrderPaidEvent.TOPIC, event.orderId(), event);
    }

    public void publishOrderCompleted(OrderCompletedEvent event) {
        kafkaTemplate.send(OrderCompletedEvent.TOPIC, event.orderId(), event);
    }
}
