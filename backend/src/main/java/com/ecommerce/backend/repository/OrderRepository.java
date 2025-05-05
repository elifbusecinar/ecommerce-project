package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderStatus;
import com.ecommerce.backend.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    
    List<Order> findByStatus(OrderStatus status);
    
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersInDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.updatedAt < :threshold")
    List<Order> findStaleOrders(
        @Param("status") OrderStatus status,
        @Param("threshold") LocalDateTime threshold
    );
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    long countByUserAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.trackingNumber = :trackingNumber")
    Order findByTrackingNumber(@Param("trackingNumber") String trackingNumber);
}
