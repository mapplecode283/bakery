package com.mooshi.order.repository;

import com.mooshi.order.model.Order;
import com.mooshi.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(String customerId);
    List<Order> findByStatus(OrderStatus status);
}
