// Path: backend/src/main/java/com/ecommerce/backend/service/CartService.java
package com.ecommerce.backend.service;

import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.model.Cart; // Yeni Cart entity'si
import com.ecommerce.backend.model.CartItem; // Yeni CartItem entity'si
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.payload.dto.CartDTO; // Yeni CartDTO
import com.ecommerce.backend.payload.dto.CartItemDTO; // Yeni CartItemDTO
import com.ecommerce.backend.repository.CartRepository; // Yeni CartRepository
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository; // UserRepository eklendi
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository; // Kullanıcıyı bulmak için

    // Kullanıcının sepetini getir veya oluştur
    private Cart getOrCreateCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return cartRepository.findByUserAndActiveTrue(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setActive(true);
                    return cartRepository.save(newCart);
                });
    }
     // Misafir kullanıcılar için (Session ID veya geçici bir ID ile)
    private Cart getOrCreateCartForGuest(String guestCartId) {
        return cartRepository.findByGuestCartIdAndActiveTrue(guestCartId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setGuestCartId(guestCartId);
                    newCart.setActive(true);
                    return cartRepository.save(newCart);
                });
    }


    public CartDTO getCartDTO(Long userId, String guestCartId) {
        Cart cart = userId != null ? getOrCreateCartForUser(userId) : getOrCreateCartForGuest(guestCartId);
        return CartDTO.fromEntity(cart);
    }

    public CartDTO addItemToCart(Long userId, String guestCartId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }

        Cart cart = userId != null ? getOrCreateCartForUser(userId) : getOrCreateCartForGuest(guestCartId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.isActive()) {
            throw new BadRequestException("Product is not active: " + product.getName());
        }
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Insufficient stock to add more quantity for product: " + product.getName());
            }
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice()); // Ürünün o anki fiyatını al
            cart.getItems().add(newItem);
        }

        cart.recalculateTotalPrice();
        Cart savedCart = cartRepository.save(cart);
        return CartDTO.fromEntity(savedCart);
    }

    public CartDTO updateItemQuantity(Long userId, String guestCartId, Long productId, int quantity) {
        Cart cart = userId != null ? getOrCreateCartForUser(userId) : getOrCreateCartForGuest(guestCartId);
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not in cart with id: " + productId));

        if (quantity <= 0) {
            cart.getItems().remove(item); // Miktar sıfır veya altıysa ürünü sepetten çıkar
        } else {
            Product product = item.getProduct();
            if (product.getStockQuantity() < quantity) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            item.setQuantity(quantity);
        }

        cart.recalculateTotalPrice();
        Cart savedCart = cartRepository.save(cart);
        return CartDTO.fromEntity(savedCart);
    }

    public CartDTO removeItemFromCart(Long userId, String guestCartId, Long productId) {
        Cart cart = userId != null ? getOrCreateCartForUser(userId) : getOrCreateCartForGuest(guestCartId);
        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new ResourceNotFoundException("Product not found in cart with id: " + productId);
        }
        cart.recalculateTotalPrice();
        Cart savedCart = cartRepository.save(cart);
        return CartDTO.fromEntity(savedCart);
    }

    public CartDTO clearCart(Long userId, String guestCartId) {
        Cart cart = userId != null ? getOrCreateCartForUser(userId) : getOrCreateCartForGuest(guestCartId);
        cart.getItems().clear();
        cart.recalculateTotalPrice();
        Cart savedCart = cartRepository.save(cart);
        return CartDTO.fromEntity(savedCart);
    }

    // Sipariş oluşturulduğunda sepeti pasif yapma veya silme
    public void deactivateCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));
        cart.setActive(false); // Veya sil cartRepository.delete(cart);
        cartRepository.save(cart);
    }
}