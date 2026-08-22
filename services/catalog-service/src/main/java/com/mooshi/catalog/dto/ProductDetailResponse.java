package com.mooshi.catalog.dto;

import com.mooshi.catalog.model.ProductOption;
import com.mooshi.catalog.model.ProductSize;
import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
    String id,
    String name,
    String description,
    String imageUrl,
    BigDecimal basePrice,
    String categoryId,
    boolean popular,
    List<ProductOption> options,
    List<ProductSize> sizes
) {}
