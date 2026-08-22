package com.mooshi.order.dto;

import com.mooshi.order.model.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
    String id,
    String customerId,
    OrderStatus status,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal deliveryFee,
    BigDecimal totalAmount,
    DeliveryType deliveryType,
    String notes,
    List<OrderItemResponse> items,
    List<StatusHistoryResponse> statusHistory,
    Instant createdAt,
    Instant updatedAt
) {
    public record OrderItemResponse(
        String id, String productId, String productName,
        String size, int quantity, BigDecimal unitPrice,
        BigDecimal subtotal, String optionsJson
    ) {}

    public record StatusHistoryResponse(
        String id, OrderStatus status, String note, Instant createdAt
    ) {}
}
