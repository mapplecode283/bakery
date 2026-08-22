package com.mooshi.catalog.repository;

import com.mooshi.catalog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByActiveTrueOrderBySortOrderAsc();
}
