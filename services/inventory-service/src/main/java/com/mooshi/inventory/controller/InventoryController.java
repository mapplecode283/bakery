package com.mooshi.inventory.controller;

import com.mooshi.common.dto.ApiResponse;
import com.mooshi.inventory.model.*;
import com.mooshi.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/ingredients")
    public ResponseEntity<ApiResponse<List<Ingredient>>> getIngredients() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getIngredients()));
    }

    @GetMapping("/ingredients/{id}")
    public ResponseEntity<ApiResponse<Ingredient>> getIngredient(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getIngredient(id)));
    }

    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<InventoryStock>> adjustStock(@RequestBody Map<String, Object> request) {
        String ingredientId = (String) request.get("ingredientId");
        BigDecimal quantity = new BigDecimal(request.get("quantity").toString());
        String reason = (String) request.getOrDefault("reason", "Manual adjustment");
        return ResponseEntity.ok(ApiResponse.ok("Stock adjusted",
            inventoryService.adjustStock(ingredientId, quantity, reason)));
    }

    @GetMapping("/movements")
    public ResponseEntity<ApiResponse<List<InventoryMovement>>> getMovements() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getMovements()));
    }
}
