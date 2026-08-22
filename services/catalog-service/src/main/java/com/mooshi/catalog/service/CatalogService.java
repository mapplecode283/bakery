package com.mooshi.catalog.service;

import com.mooshi.catalog.dto.ProductDetailResponse;
import com.mooshi.catalog.dto.ProductSummary;
import com.mooshi.catalog.model.Category;
import com.mooshi.catalog.model.Product;
import com.mooshi.catalog.repository.*;
import com.mooshi.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository optionRepository;
    private final ProductSizeRepository sizeRepository;

    public List<Category> getCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    // @Cacheable(value = "products", key = "@cacheKeyBuilder.build(#categoryId, #search, #popular)", unless = "#result.isEmpty()")
    public List<ProductSummary> getProducts(String categoryId, String search, Boolean popular) {
        List<Product> products;
        if (search != null && !search.isBlank()) {
            products = productRepository.search(search);
        } else if (popular != null && popular) {
            products = productRepository.findByActiveTrueAndPopularTrueOrderByNameAsc();
        } else if (categoryId != null) {
            products = productRepository.findByActiveTrueAndCategoryIdOrderByNameAsc(categoryId);
        } else {
            products = productRepository.findByActiveTrueOrderByCreatedAtDesc();
        }
        return products.stream()
            .map(p -> new ProductSummary(p.getId(), p.getName(), p.getDescription(),
                p.getImageUrl(), p.getBasePrice(), p.getCategoryId(), p.isPopular()))
            .toList();
    }

    public ProductDetailResponse getProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return new ProductDetailResponse(
            product.getId(), product.getName(), product.getDescription(),
            product.getImageUrl(), product.getBasePrice(), product.getCategoryId(),
            product.isPopular(),
            optionRepository.findByProductId(id),
            sizeRepository.findByProductId(id)
        );
    }
}
