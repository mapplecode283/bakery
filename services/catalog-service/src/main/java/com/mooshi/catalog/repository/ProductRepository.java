package com.mooshi.catalog.repository;

import com.mooshi.catalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByActiveTrueOrderByCreatedAtDesc();

    List<Product> findByActiveTrueAndCategoryIdOrderByNameAsc(String categoryId);

    List<Product> findByActiveTrueAndPopularTrueOrderByNameAsc();

    @Query("SELECT p FROM Product p WHERE p.active = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> search(String query);
}
