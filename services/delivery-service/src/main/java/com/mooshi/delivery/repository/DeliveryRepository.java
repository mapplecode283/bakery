package com.mooshi.delivery.repository;

import com.mooshi.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    Optional<Delivery> findByOrderId(String orderId);
    List<Delivery> findByDriverId(String driverId);
    List<Delivery> findByStatus(String status);
}
