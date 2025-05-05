package com.ecommerce.backend.payload.dto;

import lombok.*;
import java.math.BigDecimal;
import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.model.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Long productId;

    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .productId(orderItem.getProduct().getId())
                .build();
    }
    
    public OrderItem toEntity() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(this.id);
        orderItem.setQuantity(this.quantity);
        orderItem.setUnitPrice(this.unitPrice);
        orderItem.setTotalPrice(this.totalPrice);
        return orderItem;
    }
}
