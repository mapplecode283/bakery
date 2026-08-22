package com.mooshi.payment.event;

import com.mooshi.event.PaymentCompletedEvent;
import com.mooshi.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        kafkaTemplate.send(PaymentCompletedEvent.TOPIC, event.orderId(), event);
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        kafkaTemplate.send(PaymentFailedEvent.TOPIC, event.orderId(), event);
    }
}
