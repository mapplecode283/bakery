package com.mooshi.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mooshi.common.exception.BusinessException;
import com.mooshi.common.exception.ResourceNotFoundException;
import com.mooshi.event.OrderCompletedEvent;
import com.mooshi.event.OrderCreatedEvent;
import com.mooshi.event.OrderPaidEvent;
import com.mooshi.order.dto.*;
import com.mooshi.order.event.OrderEventPublisher;
import com.mooshi.order.model.*;
import com.mooshi.order.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final OrderEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CART_KEY_PREFIX = "cart:";

    public List<CartItemRequest> getCart(String customerId) {
        Object cart = redisTemplate.opsForValue().get(CART_KEY_PREFIX + customerId);
        if (cart == null) return List.of();
        try {
            return objectMapper.readValue(
                cart.toString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CartItemRequest.class)
            );
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    public void saveCart(String customerId, List<CartItemRequest> items) {
        try {
            String json = objectMapper.writeValueAsString(items);
            redisTemplate.opsForValue().set(CART_KEY_PREFIX + customerId, json, 7, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Failed to save cart");
        }
    }

    public void clearCart(String customerId) {
        redisTemplate.delete(CART_KEY_PREFIX + customerId);
    }

    @Transactional
    public OrderResponse placeOrder(String customerId, PlaceOrderRequest request) {
        // Validate items
        if (request.items().isEmpty()) {
            throw new BusinessException("Order must have at least one item");
        }

        Order order = Order.builder()
            .customerId(customerId)
            .status(OrderStatus.PAYMENT_PENDING)
            .subtotal(request.subtotal())
            .tax(request.tax())
            .deliveryFee(request.deliveryFee())
            .totalAmount(request.totalAmount())
            .deliveryType(request.deliveryType())
            .deliveryAddressId(request.deliveryAddressId())
            .notes(request.notes())
            .build();
        order = orderRepository.save(order);

        // Save order items
        for (CartItemRequest itemReq : request.items()) {
            OrderItem item = OrderItem.builder()
                .orderId(order.getId())
                .productId(itemReq.productId())
                .productName(itemReq.productName())
                .size(itemReq.size())
                .quantity(itemReq.quantity())
                .unitPrice(itemReq.unitPrice())
                .subtotal(itemReq.unitPrice()
                    .multiply(BigDecimal.valueOf(itemReq.quantity())))
                .build();
            try {
                if (itemReq.options() != null && !itemReq.options().isEmpty()) {
                    item.setOptionsJson(objectMapper.writeValueAsString(itemReq.options()));
                }
            } catch (JsonProcessingException ignored) {}
            orderItemRepository.save(item);
        }

        // Add status history
        addStatusHistory(order.getId(), OrderStatus.CREATED, "Order created");
        addStatusHistory(order.getId(), OrderStatus.PAYMENT_PENDING, "Waiting for payment");

        // Publish event
        List<OrderCreatedEvent.OrderItem> eventItems = request.items().stream()
            .map(i -> new OrderCreatedEvent.OrderItem(i.productId(), i.productName(), i.quantity(), i.unitPrice()))
            .toList();
        eventPublisher.publishOrderCreated(new OrderCreatedEvent(
            null, order.getId(), customerId, eventItems, order.getTotalAmount(), Instant.now()
        ));

        clearCart(customerId);

        List<OrderItem> savedItems = orderItemRepository.findByOrderId(order.getId());
        List<OrderStatusHistory> history = statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(order.getId());
        return new OrderResponse(
            order.getId(), order.getCustomerId(), order.getStatus(),
            order.getSubtotal(), order.getTax(), order.getDeliveryFee(),
            order.getTotalAmount(), order.getDeliveryType(), order.getNotes(),
            savedItems.stream().map(i -> new OrderResponse.OrderItemResponse(
                i.getId(), i.getProductId(), i.getProductName(),
                i.getSize(), i.getQuantity(), i.getUnitPrice(),
                i.getSubtotal(), i.getOptionsJson())).toList(),
            history.stream().map(h -> new OrderResponse.StatusHistoryResponse(
                h.getId(), h.getStatus(), h.getNote(), h.getCreatedAt())).toList(),
            order.getCreatedAt(), order.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderSummary> getOrders(String customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
            .stream()
            .map(o -> new OrderSummary(
                o.getId(), o.getCustomerId(), o.getStatus(),
                o.getSubtotal(), o.getTax(), o.getDeliveryFee(),
                o.getTotalAmount(), o.getDeliveryType(),
                o.getItems() != null ? o.getItems().size() : 0,
                o.getCreatedAt(), o.getUpdatedAt()))
            .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(String orderId) {
        Order order = findOrderOrThrow(orderId);
        return toResponse(order);
    }

    private Order findOrderOrThrow(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderStatusHistory> history = statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(order.getId());
        return new OrderResponse(
            order.getId(), order.getCustomerId(), order.getStatus(),
            order.getSubtotal(), order.getTax(), order.getDeliveryFee(),
            order.getTotalAmount(), order.getDeliveryType(), order.getNotes(),
            items.stream().map(i -> new OrderResponse.OrderItemResponse(
                i.getId(), i.getProductId(), i.getProductName(),
                i.getSize(), i.getQuantity(), i.getUnitPrice(),
                i.getSubtotal(), i.getOptionsJson())).toList(),
            history.stream().map(h -> new OrderResponse.StatusHistoryResponse(
                h.getId(), h.getStatus(), h.getNote(), h.getCreatedAt())).toList(),
            order.getCreatedAt(), order.getUpdatedAt()
        );
    }

    @Transactional
    public OrderResponse cancelOrder(String customerId, String orderId) {
        Order order = findOrderOrThrow(orderId);
        if (!order.getCustomerId().equals(customerId)) {
            throw new BusinessException("Order does not belong to customer");
        }
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel completed order");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Order already cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        addStatusHistory(orderId, OrderStatus.CANCELLED, "Order cancelled by customer");
        orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrderOrThrow(orderId);
        order.setStatus(newStatus);
        addStatusHistory(orderId, newStatus, "Status updated");

        if (newStatus == OrderStatus.PAID) {
            eventPublisher.publishOrderPaid(new OrderPaidEvent(
                null, orderId, order.getCustomerId(), null, order.getTotalAmount(), Instant.now()
            ));
        }
        if (newStatus == OrderStatus.COMPLETED) {
            eventPublisher.publishOrderCompleted(new OrderCompletedEvent(
                null, orderId, order.getCustomerId(), newStatus.name(), Instant.now()
            ));
        }

        orderRepository.save(order);
        return toResponse(order);
    }

    public List<OrderStatusHistory> getOrderStatus(String orderId) {
        return statusHistoryRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }

    private void addStatusHistory(String orderId, OrderStatus status, String note) {
        statusHistoryRepository.save(OrderStatusHistory.builder()
            .orderId(orderId)
            .status(status)
            .note(note)
            .build());
    }
}
