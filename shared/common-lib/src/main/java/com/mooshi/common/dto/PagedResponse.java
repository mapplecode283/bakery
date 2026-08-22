package com.mooshi.common.dto;

import java.time.Instant;

public record PagedResponse<T>(
    boolean success,
    String message,
    java.util.List<T> data,
    PageInfo page,
    Instant timestamp
) {
    public record PageInfo(int page, int size, long totalElements, int totalPages) {}
}
