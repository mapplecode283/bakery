package com.mooshi.payment.controller;

import com.mooshi.common.dto.ApiResponse;
import com.mooshi.payment.model.Payment;
import com.mooshi.payment.model.Refund;
import com.mooshi.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse<Payment>> pay(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable String orderId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Payment processed",
            paymentService.processPayment(orderId, userId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Payment>> getPayment(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByOrderId(orderId)));
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<ApiResponse<Refund>> refund(
        @PathVariable String orderId,
        @RequestParam(defaultValue = "Customer request") String reason
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Refund processed",
            paymentService.refundPayment(orderId, reason)));
    }
}
