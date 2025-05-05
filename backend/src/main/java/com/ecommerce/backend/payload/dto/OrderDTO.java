package com.ecommerce.backend.payload.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.ecommerce.backend.model.Order;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private String username; // Siparişi veren kullanıcının ismi
    private List<OrderItemDTO> orderItems;
    private Long userId; // ✨ Bunu ekliyoruz!

    public static OrderDTO fromEntity(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser().getId())
                .build();
    }
    
    public Order toEntity() {
        Order order = new Order();
        order.setId(this.id);
        order.setTotalAmount(this.totalAmount);
        order.setCreatedAt(this.createdAt);
        // Kullanıcıyı buraya eklemek gerekebilir (şu an userId taşıyoruz)
        return order;
    }
    
}
