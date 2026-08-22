package com.mooshi.delivery.controller;

import com.mooshi.common.dto.ApiResponse;
import com.mooshi.delivery.model.Delivery;
import com.mooshi.delivery.model.Driver;
import com.mooshi.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Delivery>> createDelivery(@RequestBody Map<String, String> request) {
        Delivery delivery = deliveryService.createDelivery(
            request.get("orderId"),
            request.get("pickupAddress"),
            request.get("deliveryAddress")
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Delivery created", delivery));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Delivery>> getDelivery(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getDelivery(id)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Delivery>> getDeliveryByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getDeliveryByOrderId(orderId)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Delivery>> updateStatus(
        @PathVariable String id,
        @RequestBody Map<String, String> request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Status updated",
            deliveryService.updateStatus(id, request.get("status"))));
    }

    @PutMapping("/{id}/assign/{driverId}")
    public ResponseEntity<ApiResponse<Delivery>> assignDriver(
        @PathVariable String id,
        @PathVariable String driverId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Driver assigned",
            deliveryService.assignDriver(id, driverId)));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Delivery>>> getPending() {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getPendingDeliveries()));
    }

    @GetMapping("/drivers/available")
    public ResponseEntity<ApiResponse<List<Driver>>> getAvailableDrivers() {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getAvailableDrivers()));
    }
}
