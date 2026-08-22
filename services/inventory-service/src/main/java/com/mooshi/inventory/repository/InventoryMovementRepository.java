package com.mooshi.inventory.repository;

import com.mooshi.inventory.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, String> {
    List<InventoryMovement> findByIngredientIdOrderByCreatedAtDesc(String ingredientId);
    List<InventoryMovement> findAllByOrderByCreatedAtDesc();
}
