package com.mooshi.catalog.repository;

import com.mooshi.catalog.model.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductSizeRepository extends JpaRepository<ProductSize, String> {
    List<ProductSize> findByProductId(String productId);
}
