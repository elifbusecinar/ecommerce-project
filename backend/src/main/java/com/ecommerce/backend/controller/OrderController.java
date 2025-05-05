package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderStatus;
import com.ecommerce.backend.model.PaymentStatus;
import com.ecommerce.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Order", description = "Order management APIs")
public class OrderController {
    private final OrderService orderService;

    @Operation(
        summary = "Create new order",
        description = "Creates a new order for the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created order"),
        @ApiResponse(responseCode = "400", description = "Invalid order data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Product not found or out of stock")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> createOrder(
        @Parameter(description = "Order details to create") @RequestBody Order order
    ) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view this order")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> getOrderById(
        @Parameter(description = "ID of the order to retrieve") @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(
        summary = "Get order by tracking number",
        description = "Retrieves an order using its tracking number"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved order"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Order> getOrderByTrackingNumber(
        @Parameter(description = "Tracking number of the order") @PathVariable String trackingNumber
    ) {
        return ResponseEntity.ok(orderService.getOrderByTrackingNumber(trackingNumber));
    }

    @Operation(
        summary = "Get user orders",
        description = "Retrieves all orders for a specific user with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user orders"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view these orders")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Order>> getUserOrders(
        @Parameter(description = "ID of the user") @PathVariable Long userId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @Operation(
        summary = "Update order status",
        description = "Updates the status of an existing order (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated order status"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(
        @Parameter(description = "ID of the order to update") @PathVariable Long id,
        @Parameter(description = "New order status") @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @Operation(
        summary = "Update payment status",
        description = "Updates the payment status of an existing order (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated payment status"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updatePaymentStatus(
        @Parameter(description = "ID of the order to update") @PathVariable Long id,
        @Parameter(description = "New payment status") @RequestParam PaymentStatus paymentStatus
    ) {
        return ResponseEntity.ok(orderService.updatePaymentStatus(id, paymentStatus));
    }

    @Operation(
        summary = "Get orders by status",
        description = "Retrieves all orders with a specific status (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByStatus(
        @Parameter(description = "Status to filter orders by") @PathVariable OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @Operation(
        summary = "Get orders by payment status",
        description = "Retrieves all orders with a specific payment status (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/payment-status/{paymentStatus}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByPaymentStatus(
        @Parameter(description = "Payment status to filter orders by") @PathVariable PaymentStatus paymentStatus
    ) {
        return ResponseEntity.ok(orderService.getOrdersByPaymentStatus(paymentStatus));
    }

    @Operation(
        summary = "Get orders in date range",
        description = "Retrieves all orders within a specific date range (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
        @ApiResponse(responseCode = "400", description = "Invalid date range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersInDateRange(
        @Parameter(description = "Start date of the range") @RequestParam LocalDateTime startDate,
        @Parameter(description = "End date of the range") @RequestParam LocalDateTime endDate
    ) {
        return ResponseEntity.ok(orderService.getOrdersInDateRange(startDate, endDate));
    }

    @Operation(
        summary = "Get stale orders",
        description = "Retrieves orders that have been in a specific status for too long (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved stale orders"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/stale")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getStaleOrders(
        @Parameter(description = "Status to check for staleness") @RequestParam OrderStatus status,
        @Parameter(description = "Time threshold for considering an order stale") @RequestParam LocalDateTime threshold
    ) {
        return ResponseEntity.ok(orderService.getStaleOrders(status, threshold));
    }

    @Operation(
        summary = "Count user orders",
        description = "Counts the number of orders for a user with a specific status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully counted orders"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to view these orders")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{userId}/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countUserOrders(
        @Parameter(description = "ID of the user") @PathVariable Long userId,
        @Parameter(description = "Status to count orders by") @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(orderService.countUserOrders(userId, status));
    }
}
