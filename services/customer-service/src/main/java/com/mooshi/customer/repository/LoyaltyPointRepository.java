package com.mooshi.customer.repository;

import com.mooshi.customer.model.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, String> {
    List<LoyaltyPoint> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    int countByCustomerId(String customerId);
}
