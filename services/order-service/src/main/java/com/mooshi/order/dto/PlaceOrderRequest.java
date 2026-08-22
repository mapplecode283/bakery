package com.mooshi.order.dto;

import com.mooshi.order.model.DeliveryType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record PlaceOrderRequest(
    @NotNull DeliveryType deliveryType,
    String deliveryAddressId,
    String notes,
    @NotEmpty List<CartItemRequest> items,
    BigDecimal subtotal,
    BigDecimal tax,
    BigDecimal deliveryFee,
    BigDecimal totalAmount
) {}
