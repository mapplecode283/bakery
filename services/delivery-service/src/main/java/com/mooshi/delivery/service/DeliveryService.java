package com.mooshi.delivery.service;

import com.mooshi.common.exception.BusinessException;
import com.mooshi.common.exception.ResourceNotFoundException;
import com.mooshi.delivery.model.Delivery;
import com.mooshi.delivery.model.Driver;
import com.mooshi.delivery.repository.DeliveryRepository;
import com.mooshi.delivery.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public Delivery createDelivery(String orderId, String pickupAddress, String deliveryAddress) {
        Delivery delivery = Delivery.builder()
            .orderId(orderId)
            .pickupAddress(pickupAddress)
            .deliveryAddress(deliveryAddress)
            .status("PENDING")
            .build();
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery assignDriver(String deliveryId, String driverId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", deliveryId));
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        if (!"ONLINE".equals(driver.getStatus())) {
            throw new BusinessException("Driver is not available");
        }

        delivery.setDriverId(driverId);
        delivery.setStatus("ASSIGNED");
        driver.setStatus("BUSY");
        driverRepository.save(driver);

        log.info("Delivery {} assigned to driver {}", deliveryId, driverId);
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery updateStatus(String deliveryId, String status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", deliveryId));

        switch (status) {
            case "PICKED_UP" -> delivery.setPickupAt(Instant.now());
            case "DELIVERED" -> {
                delivery.setDeliveredAt(Instant.now());
                delivery.getDriverId(); // release driver
            }
        }
        delivery.setStatus(status);

        // Release driver on delivery complete
        if ("DELIVERED".equals(status) && delivery.getDriverId() != null) {
            driverRepository.findById(delivery.getDriverId()).ifPresent(driver -> {
                driver.setStatus("ONLINE");
                driverRepository.save(driver);
            });
        }

        return deliveryRepository.save(delivery);
    }

    public Delivery getDelivery(String id) {
        return deliveryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery", "id", id));
    }

    public Delivery getDeliveryByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Delivery for order not found"));
    }

    public List<Delivery> getPendingDeliveries() {
        return deliveryRepository.findByStatus("PENDING");
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByStatus("ONLINE");
    }
}
