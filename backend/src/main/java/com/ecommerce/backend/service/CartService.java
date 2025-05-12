package com.ecommerce.backend.service;
 
import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.payload.dto.OrderItemDTO;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class CartService {
 
    private List<OrderItem> cart = new ArrayList<>(); // Bu hala geçici, kullanıcıya özel olmalı
    private final ProductService productService;
 
    public OrderItemDTO addToCart(Long productId, Integer quantity) { // Parametreleri productId ve quantity olarak güncelledik
        Product product = productService.getProductById(productId);
 
        if (product == null || !product.isActive()) {
            throw new ResourceNotFoundException("Product not found or not active with id: " + productId);
        }
 
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero.");
        }
 
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }
 
        Optional<OrderItem> existingItemOpt = cart.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
 
        OrderItem itemToReturn;
 
        if (existingItemOpt.isPresent()) {
            OrderItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;
 
            if (product.getStockQuantity() < newQuantity) {
                throw new BadRequestException("Insufficient stock to add more quantity for product: " + product.getName());
            }
            existingItem.setQuantity(newQuantity);
            existingItem.calculateTotalPrice();
            itemToReturn = existingItem;
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            newItem.calculateTotalPrice();
            cart.add(newItem);
            itemToReturn = newItem;
        }
        return OrderItemDTO.fromEntity(itemToReturn);
    }
 
    public List<OrderItemDTO> viewCart() {
        return cart.stream()
                .map(OrderItemDTO::fromEntity)
                .collect(Collectors.toList());
    }
 
    public void removeFromCart(Long productId) {
        cart.removeIf(item -> item.getProduct().getId().equals(productId));
    }
 
    public OrderItemDTO updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(productId);
            // Bu durumda null dönmek veya bir exception fırlatmak daha uygun olabilir.
            // Frontend'in bu durumu nasıl ele alacağına bağlı.
            // Şimdilik, ürün kaldırıldığı için null dönelim veya boş bir sepet objesi.
            return null;
        }
 
        Optional<OrderItem> itemOpt = cart.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
 
        if (itemOpt.isPresent()) {
            OrderItem item = itemOpt.get();
            Product product = productService.getProductById(productId);
            if (product.getStockQuantity() < quantity) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            item.setQuantity(quantity);
            item.calculateTotalPrice();
            return OrderItemDTO.fromEntity(item);
        } else {
            throw new ResourceNotFoundException("Product not in cart with id: " + productId);
        }
    }
 
    //********* YENİ EKLENEN METOD *********
    public void clearCart() {
        this.cart.clear();
        // Eğer kullanıcıya özel sepet yönetimi varsa,
        // o kullanıcının sepetini veritabanından veya session'dan temizleme işlemleri burada yapılır.
        // Örneğin: cartRepository.deleteAllByUserId(userId);
    }
    //****************************************
 
    public String checkout() {
        // Bu metodun gerçek işlevi OrderService'e taşınmalı.
        // Şimdilik basit bir temizleme ve mesaj döndürme işlemi yapıyor.
        clearCart(); // Sepeti temizle
        return "Checkout successful! 🛒✔️";
    }
}