package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.payload.dto.OrderItemDTO;
import com.ecommerce.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public OrderItemDTO addToCart(@RequestBody OrderItemDTO orderItemDTO) {
        return cartService.addToCart(orderItemDTO);
    }

    @GetMapping
    public List<OrderItemDTO> viewCart() {
        return cartService.viewCart();
    }

    @DeleteMapping("/remove/{id}")
    public void removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
    }

    @PostMapping("/checkout")
    public String checkout() {
        return cartService.checkout();
    }
}
