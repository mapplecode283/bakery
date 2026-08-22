package com.mooshi.customer.service;

import com.mooshi.common.exception.ResourceNotFoundException;
import com.mooshi.customer.dto.*;
import com.mooshi.customer.model.*;
import com.mooshi.customer.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final FavoriteRepository favoriteRepository;
    private final LoyaltyPointRepository loyaltyPointRepository;

    public Customer getByUserId(String userId) {
        return customerRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "userId", userId));
    }

    @Transactional
    public Customer updateProfile(String userId, UpdateProfileRequest request) {
        Customer customer = getByUserId(userId);
        if (request.phone() != null) customer.setPhone(request.phone());
        if (request.dateOfBirth() != null) customer.setDateOfBirth(request.dateOfBirth());
        return customerRepository.save(customer);
    }

    public List<CustomerAddress> getAddresses(String userId) {
        Customer customer = getByUserId(userId);
        return addressRepository.findByCustomerId(customer.getId());
    }

    @Transactional
    public CustomerAddress addAddress(String userId, AddressRequest request) {
        Customer customer = getByUserId(userId);
        if (request.isDefault()) {
            addressRepository.clearDefaults(customer.getId());
        }
        CustomerAddress address = CustomerAddress.builder()
            .customerId(customer.getId())
            .label(request.label())
            .street(request.street())
            .city(request.city())
            .state(request.state())
            .zipCode(request.zipCode())
            .country(request.country() != null ? request.country() : "Malaysia")
            .isDefault(request.isDefault())
            .build();
        return addressRepository.save(address);
    }

    @Transactional
    public CustomerAddress updateAddress(String userId, String addressId, AddressRequest request) {
        CustomerAddress address = addressRepository.findById(addressId)
            .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (request.isDefault()) {
            addressRepository.clearDefaults(address.getCustomerId());
        }
        if (request.label() != null) address.setLabel(request.label());
        if (request.street() != null) address.setStreet(request.street());
        if (request.city() != null) address.setCity(request.city());
        if (request.state() != null) address.setState(request.state());
        if (request.zipCode() != null) address.setZipCode(request.zipCode());
        address.setDefault(request.isDefault());
        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(String userId, String addressId) {
        Customer customer = getByUserId(userId);
        addressRepository.deleteById(addressId);
    }

    public List<Favorite> getFavorites(String userId) {
        Customer customer = getByUserId(userId);
        return favoriteRepository.findByCustomerId(customer.getId());
    }

    @Transactional
    public Favorite addFavorite(String userId, String productId, String productName) {
        Customer customer = getByUserId(userId);
        if (favoriteRepository.existsByCustomerIdAndProductId(customer.getId(), productId)) {
            return favoriteRepository.findByCustomerIdAndProductId(customer.getId(), productId).get();
        }
        Favorite favorite = Favorite.builder()
            .customerId(customer.getId())
            .productId(productId)
            .productName(productName)
            .build();
        return favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(String userId, String productId) {
        Customer customer = getByUserId(userId);
        favoriteRepository.findByCustomerIdAndProductId(customer.getId(), productId)
            .ifPresent(favoriteRepository::delete);
    }

    public List<LoyaltyPoint> getLoyaltyHistory(String userId) {
        Customer customer = getByUserId(userId);
        return loyaltyPointRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
    }
}
