package com.mooshi.catalog.config;

import org.springframework.stereotype.Component;

@Component
public class CacheKeyBuilder {

    public String build(String categoryId, String search, Boolean popular) {
        StringBuilder sb = new StringBuilder("products");
        if (categoryId != null) sb.append(":").append(categoryId);
        if (search != null) sb.append(":search:").append(search.toLowerCase());
        if (popular != null && popular) sb.append(":popular");
        else if (categoryId == null && search == null) sb.append(":all");
        return sb.toString();
    }
}
