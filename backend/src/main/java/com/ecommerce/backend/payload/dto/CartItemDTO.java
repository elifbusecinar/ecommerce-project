package com.ecommerce.backend.payload.dto;

import com.ecommerce.backend.model.CartItem;
import com.ecommerce.backend.model.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItemDTO {
    private Long id; // CartItem ID'si
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal; // quantity * unitPrice

    public static CartItemDTO fromEntity(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        if (cartItem.getProduct() != null) {
            Product product = cartItem.getProduct();
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setProductImageUrl(product.getImageUrl()); // Ana görsel
        }
        dto.setQuantity(cartItem.getQuantity());
        dto.setUnitPrice(cartItem.getUnitPrice());
        dto.setSubtotal(cartItem.getSubtotal());
        return dto;
    }
}