package com.mooshi.customer.repository;

import com.mooshi.customer.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    List<Favorite> findByCustomerId(String customerId);
    Optional<Favorite> findByCustomerIdAndProductId(String customerId, String productId);
    boolean existsByCustomerIdAndProductId(String customerId, String productId);
}
