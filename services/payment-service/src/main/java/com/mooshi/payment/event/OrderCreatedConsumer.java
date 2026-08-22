package com.mooshi.payment.event;

import com.mooshi.event.OrderCreatedEvent;
import com.mooshi.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = OrderCreatedEvent.TOPIC, groupId = "payment-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for orderId={}", event.orderId());
        paymentService.processOrderPayment(event);
    }
}
