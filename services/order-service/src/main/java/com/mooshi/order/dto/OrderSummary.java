package com.mooshi.order.dto;

import com.mooshi.order.model.DeliveryType;
import com.mooshi.order.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummary(
    String id,
    String customerId,
    OrderStatus status,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal deliveryFee,
    BigDecimal totalAmount,
    DeliveryType deliveryType,
    int itemCount,
    Instant createdAt,
    Instant updatedAt
) {}
