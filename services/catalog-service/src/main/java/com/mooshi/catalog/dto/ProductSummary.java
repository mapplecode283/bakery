package com.mooshi.catalog.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ProductSummary(
    String id,
    String name,
    String description,
    String imageUrl,
    BigDecimal basePrice,
    String categoryId,
    boolean popular
) implements Serializable {}
