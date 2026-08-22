package com.mooshi.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record CartItemRequest(
    @NotBlank String productId,
    @NotBlank String productName,
    String size,
    @Positive int quantity,
    BigDecimal unitPrice,
    List<OptionSelection> options
) {
    public record OptionSelection(String optionId, String name, BigDecimal priceAdjustment) {}
}
