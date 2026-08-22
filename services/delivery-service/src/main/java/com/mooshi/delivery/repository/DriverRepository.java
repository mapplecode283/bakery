package com.mooshi.delivery.repository;

import com.mooshi.delivery.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, String> {
    List<Driver> findByStatus(String status);
}
