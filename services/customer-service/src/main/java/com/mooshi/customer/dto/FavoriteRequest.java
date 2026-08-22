package com.mooshi.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record FavoriteRequest(@NotBlank String productId, @NotBlank String productName) {}
