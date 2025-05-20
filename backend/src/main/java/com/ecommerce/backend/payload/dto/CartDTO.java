package com.ecommerce.backend.payload.dto;

import com.ecommerce.backend.model.Cart;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CartDTO {
    private Long id;
    private Long userId; // İlişkili kullanıcı ID'si (varsa)
    private String guestCartId; // Misafir sepet ID'si (varsa)
    private List<CartItemDTO> items;
    private BigDecimal totalPrice;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartDTO fromEntity(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        if (cart.getUser() != null) {
            dto.setUserId(cart.getUser().getId());
        }
        dto.setGuestCartId(cart.getGuestCartId());
        dto.setItems(cart.getItems().stream()
                .map(CartItemDTO::fromEntity)
                .collect(Collectors.toList()));
        dto.setTotalPrice(cart.getTotalPrice());
        dto.setActive(cart.isActive());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());
        return dto;
    }
}