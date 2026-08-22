package com.mooshi.inventory.repository;

import com.mooshi.inventory.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, String> {}
