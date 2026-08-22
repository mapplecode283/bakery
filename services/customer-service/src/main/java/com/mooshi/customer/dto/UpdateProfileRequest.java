package com.mooshi.customer.dto;

import java.time.LocalDate;

public record UpdateProfileRequest(String phone, LocalDate dateOfBirth) {}
