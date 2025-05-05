package com.ecommerce.backend.service;

import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.payload.dto.OrderItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private List<OrderItem> cart = new ArrayList<>();

    public OrderItemDTO addToCart(OrderItemDTO orderItemDTO) {
        OrderItem item = new OrderItem();
        Product product = new Product();
        product.setId(orderItemDTO.getProductId());
        item.setProduct(product);
        item.setQuantity(orderItemDTO.getQuantity());
        item.setUnitPrice(orderItemDTO.getUnitPrice());
        item.calculateTotalPrice();
        cart.add(item);
        return OrderItemDTO.fromEntity(item);
    }

    public List<OrderItemDTO> viewCart() {
        return cart.stream()
                .map(OrderItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void removeFromCart(Long id) {
        cart.removeIf(item -> item.getId().equals(id));
    }

    public String checkout() {
        cart.clear();
        return "Checkout successful! 🛒✔️";
    }
}
