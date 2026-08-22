package com.mooshi.inventory.repository;

import com.mooshi.inventory.model.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, String> {
    Optional<InventoryStock> findByIngredientId(String ingredientId);
}
