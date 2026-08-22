package com.mooshi.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    @NotBlank String label,
    @NotBlank String street,
    @NotBlank String city,
    String state,
    @NotBlank String zipCode,
    String country,
    boolean isDefault
) {}
