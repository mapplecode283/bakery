package com.mooshi.notification.event;

import com.mooshi.event.*;
import com.mooshi.notification.model.Notification;
import com.mooshi.notification.model.NotificationType;
import com.mooshi.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = UserRegisteredEvent.TOPIC, groupId = "notification-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Sending welcome notification to userId={}", event.userId());
        notificationRepository.save(Notification.builder()
            .userId(event.userId())
            .type(NotificationType.EMAIL)
            .title("Welcome to Mooshi!")
            .body("Hi " + event.firstName() + ", welcome to Mooshi Coffee! Start exploring our menu.")
            .build());
    }

    @KafkaListener(topics = OrderCreatedEvent.TOPIC, groupId = "notification-service")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Sending order confirmation for orderId={}", event.orderId());
        notificationRepository.save(Notification.builder()
            .userId(event.customerId())
            .type(NotificationType.PUSH)
            .title("Order Confirmed")
            .body("Your order #" + event.orderId() + " has been placed. Total: RM" + event.totalAmount())
            .build());
    }

    @KafkaListener(topics = PaymentCompletedEvent.TOPIC, groupId = "notification-service")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Sending payment confirmation for orderId={}", event.orderId());
        notificationRepository.save(Notification.builder()
            .userId(event.customerId())
            .type(NotificationType.PUSH)
            .title("Payment Successful")
            .body("Payment of RM" + event.amount() + " received. We're preparing your order!")
            .build());
    }

    @KafkaListener(topics = OrderCompletedEvent.TOPIC, groupId = "notification-service")
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("Sending order completed notification for orderId={}", event.orderId());
        notificationRepository.save(Notification.builder()
            .userId(event.customerId())
            .type(NotificationType.EMAIL)
            .title("Order Complete!")
            .body("Your order #" + event.orderId() + " is complete. Enjoy your coffee!")
            .build());
    }
}
