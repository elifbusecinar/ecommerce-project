package com.ecommerce.backend.service;

import com.ecommerce.backend.model.*;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public Order createOrder(Order order) {
        // Validate and update product stock
        validateAndUpdateStock(order);
        
        // Generate tracking number
        order.setTrackingNumber(generateTrackingNumber());
        
        // Set initial status
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        
        // Calculate total amount
        BigDecimal totalAmount = calculateTotalAmount(order.getItems());
        order.setTotalAmount(totalAmount);
        
        return orderRepository.save(order);
    }

    private void validateAndUpdateStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = productService.getProductById(item.getProduct().getId());
            
            if (!productService.isInStock(product.getId(), item.getQuantity())) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            
            // Update stock
            productService.updateStock(product.getId(), -item.getQuantity());
            
            // Set the unit price from current product price
            item.setUnitPrice(product.getPrice());
        }
    }

    private String generateTrackingNumber() {
        return "TN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public Order getOrderByTrackingNumber(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with tracking number: " + trackingNumber);
        }
        return order;
    }

    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        Order order = getOrderById(id);
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getOrdersByPaymentStatus(PaymentStatus paymentStatus) {
        return orderRepository.findByPaymentStatus(paymentStatus);
    }

    public List<Order> getOrdersInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrdersInDateRange(startDate, endDate);
    }

    public List<Order> getStaleOrders(OrderStatus status, LocalDateTime threshold) {
        return orderRepository.findStaleOrders(status, threshold);
    }

    public long countUserOrders(Long userId, OrderStatus status) {
        return orderRepository.countByUserAndStatus(userId, status);
    }
}
