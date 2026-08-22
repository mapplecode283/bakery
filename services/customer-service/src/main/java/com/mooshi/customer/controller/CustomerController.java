package com.mooshi.customer.controller;

import com.mooshi.common.dto.ApiResponse;
import com.mooshi.customer.dto.*;
import com.mooshi.customer.model.*;
import com.mooshi.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Customer>> getProfile(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getByUserId(userId)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Customer>> updateProfile(
        @RequestHeader("X-User-Id") String userId,
        @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", customerService.updateProfile(userId, request)));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<CustomerAddress>>> getAddresses(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getAddresses(userId)));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<CustomerAddress>> addAddress(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody AddressRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Address added", customerService.addAddress(userId, request)));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<CustomerAddress>> updateAddress(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable String id,
        @RequestBody AddressRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Address updated", customerService.updateAddress(userId, id, request)));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable String id
    ) {
        customerService.deleteAddress(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Address deleted", null));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<Favorite>>> getFavorites(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getFavorites(userId)));
    }

    @PostMapping("/favorites")
    public ResponseEntity<ApiResponse<Favorite>> addFavorite(
        @RequestHeader("X-User-Id") String userId,
        @Valid @RequestBody FavoriteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created("Favorite added", customerService.addFavorite(userId, request.productId(), request.productName())));
    }

    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
        @RequestHeader("X-User-Id") String userId,
        @PathVariable String productId
    ) {
        customerService.removeFavorite(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok("Favorite removed", null));
    }

    @GetMapping("/loyalty")
    public ResponseEntity<ApiResponse<List<LoyaltyPoint>>> getLoyalty(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(customerService.getLoyaltyHistory(userId)));
    }
}
