package com.mooshi.payment.service;

import com.mooshi.common.exception.BusinessException;
import com.mooshi.common.exception.ResourceNotFoundException;
import com.mooshi.event.OrderCreatedEvent;
import com.mooshi.event.PaymentCompletedEvent;
import com.mooshi.event.PaymentFailedEvent;
import com.mooshi.payment.event.PaymentEventPublisher;
import com.mooshi.payment.model.*;
import com.mooshi.payment.repository.PaymentRepository;
import com.mooshi.payment.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final PaymentEventPublisher eventPublisher;

    @Transactional
    public void processOrderPayment(OrderCreatedEvent event) {
        // Check if payment already exists
        if (paymentRepository.findByOrderId(event.orderId()).isPresent()) {
            log.info("Payment already exists for orderId={}", event.orderId());
            return;
        }

        // Mock payment processing - ~90% success rate
        boolean success = Math.random() < 0.90;

        Payment payment = Payment.builder()
            .orderId(event.orderId())
            .customerId(event.customerId())
            .amount(event.totalAmount())
            .method(PaymentMethod.CARD)
            .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .build();

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment = paymentRepository.save(payment);
            log.info("Payment completed for orderId={}, amount={}", event.orderId(), event.totalAmount());

            eventPublisher.publishPaymentCompleted(new PaymentCompletedEvent(
                null, event.orderId(), event.customerId(),
                payment.getId(), event.totalAmount(), Instant.now()
            ));
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);
            log.warn("Payment failed for orderId={}", event.orderId());

            eventPublisher.publishPaymentFailed(new PaymentFailedEvent(
                null, event.orderId(), event.customerId(),
                event.totalAmount(), "Insufficient funds", Instant.now()
            ));
        }
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment for order not found"));
    }

    @Transactional
    public Payment processPayment(String orderId, String customerId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseGet(() -> Payment.builder()
                .orderId(orderId)
                .customerId(customerId)
                .amount(BigDecimal.ZERO)
                .build());

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessException("Payment already completed");
        }

        boolean success = Math.random() < 0.95;
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment = paymentRepository.save(payment);
            eventPublisher.publishPaymentCompleted(new PaymentCompletedEvent(
                null, orderId, customerId, payment.getId(), payment.getAmount(), Instant.now()
            ));
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentRepository.save(payment);
            eventPublisher.publishPaymentFailed(new PaymentFailedEvent(
                null, orderId, customerId, payment.getAmount(), "Transaction declined", Instant.now()
            ));
        }
        return payment;
    }

    @Transactional
    public Refund refundPayment(String orderId, String reason) {
        Payment payment = getPaymentByOrderId(orderId);
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException("Can only refund completed payments");
        }

        Refund refund = Refund.builder()
            .paymentId(payment.getId())
            .orderId(orderId)
            .amount(payment.getAmount())
            .reason(reason)
            .status("COMPLETED")
            .build();
        refund = refundRepository.save(refund);

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        return refund;
    }
}
