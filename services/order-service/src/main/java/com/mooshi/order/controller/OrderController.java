package com.mooshi.order.controller;

import com.mooshi.common.dto.ApiResponse;
import com.mooshi.order.dto.*;
import com.mooshi.order.model.*;
import com.mooshi.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<CartItemRequest>>> getCart(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getCart(userId)));
    }

    @PostMapping("/cart")
    public ResponseEntity<ApiResponse<Void>> saveCart(
        @RequestHeader("X-User-Id") String userId,
        @RequestBody List<CartItemRequest> items
    ) {
        orderService.saveCart(userId, items);
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", null));
    }

    @DeleteMapping("/cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestHeader("X-User-Id") String userId) {
        orderService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody PlaceOrderRequest request
    ) {
        OrderResponse order = orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Order placed successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderSummary>>> getOrders(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrders(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrder(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled", orderService.cancelOrder(userId, id)));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<List<OrderStatusHistory>>> getOrderStatus(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderStatus(id)));
    }
}
