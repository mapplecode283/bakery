package com.mooshi.inventory.service;

import com.mooshi.common.exception.BusinessException;
import com.mooshi.common.exception.ResourceNotFoundException;
import com.mooshi.event.InventoryLowEvent;
import com.mooshi.event.OrderCreatedEvent;
import com.mooshi.inventory.event.InventoryEventPublisher;
import com.mooshi.inventory.model.*;
import com.mooshi.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final IngredientRepository ingredientRepository;
    private final InventoryStockRepository stockRepository;
    private final InventoryMovementRepository movementRepository;
    private final InventoryEventPublisher eventPublisher;

    @Transactional
    public void deductForOrder(OrderCreatedEvent event) {
        // Mock: deduct a small amount from coffee and milk for each order
        List<String> affectedIngredients = List.of("ing-coffee-beans", "ing-milk");
        for (String ingId : affectedIngredients) {
            stockRepository.findByIngredientId(ingId).ifPresent(stock -> {
                BigDecimal deduction = new BigDecimal("0.05"); // 50g coffee / 50ml milk
                if (stock.getCurrentStock().compareTo(deduction) < 0) {
                    log.warn("Insufficient stock for ingredient={}", ingId);
                    return;
                }
                stock.setCurrentStock(stock.getCurrentStock().subtract(deduction));
                stock.setUpdatedAt(Instant.now());
                stockRepository.save(stock);

                movementRepository.save(InventoryMovement.builder()
                    .id(UUID.randomUUID().toString())
                    .ingredientId(ingId)
                    .movementType(MovementType.OUT)
                    .quantity(deduction)
                    .reason("Order #" + event.orderId())
                    .referenceId(event.orderId())
                    .build());

                // Check if below minimum stock
                Ingredient ingredient = ingredientRepository.findById(ingId).orElse(null);
                if (ingredient != null && stock.getCurrentStock().compareTo(ingredient.getMinStock()) <= 0) {
                    log.warn("Low stock alert for {}", ingredient.getName());
                    eventPublisher.publishInventoryLow(new InventoryLowEvent(
                        null, ingId, ingredient.getName(),
                        stock.getCurrentStock(), ingredient.getMinStock(), Instant.now()
                    ));
                }
            });
        }
    }

    public List<Ingredient> getIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredient(String id) {
        return ingredientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
    }

    @Transactional
    public InventoryStock adjustStock(String ingredientId, BigDecimal quantity, String reason) {
        Ingredient ingredient = getIngredient(ingredientId);
        InventoryStock stock = stockRepository.findByIngredientId(ingredientId)
            .orElseThrow(() -> new ResourceNotFoundException("Stock", "ingredientId", ingredientId));

        MovementType movementType = quantity.compareTo(BigDecimal.ZERO) >= 0 ? MovementType.IN : MovementType.OUT;
        BigDecimal absQuantity = quantity.abs();

        if (movementType == MovementType.OUT && stock.getCurrentStock().compareTo(absQuantity) < 0) {
            throw new BusinessException("Insufficient stock");
        }

        if (movementType == MovementType.IN) {
            stock.setCurrentStock(stock.getCurrentStock().add(absQuantity));
        } else {
            stock.setCurrentStock(stock.getCurrentStock().subtract(absQuantity));
        }
        stock.setUpdatedAt(Instant.now());
        stock = stockRepository.save(stock);

        movementRepository.save(InventoryMovement.builder()
            .id(UUID.randomUUID().toString())
            .ingredientId(ingredientId)
            .movementType(movementType)
            .quantity(absQuantity)
            .reason(reason)
            .build());

        return stock;
    }

    public List<InventoryMovement> getMovements() {
        return movementRepository.findAllByOrderByCreatedAtDesc();
    }
}
