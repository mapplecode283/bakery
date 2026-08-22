package com.mooshi.catalog.controller;

import com.mooshi.catalog.dto.ProductDetailResponse;
import com.mooshi.catalog.dto.ProductSummary;
import com.mooshi.catalog.model.Category;
import com.mooshi.catalog.service.CatalogService;
import com.mooshi.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved", catalogService.getCategories()));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductSummary>>> getProducts(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Boolean popular
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved",
            catalogService.getProducts(category, search, popular)));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Product retrieved", catalogService.getProduct(id)));
    }
}
