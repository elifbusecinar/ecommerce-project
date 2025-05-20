package com.ecommerce.backend.controller;
 
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.payload.dto.CartDTO;
import com.ecommerce.backend.payload.dto.OrderItemDTO;
import com.ecommerce.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Eğer kullanıcıya özel sepet için gerekirse
// import org.springframework.security.core.annotation.AuthenticationPrincipal; // Kullanıcıya özel sepet için
// import com.ecommerce.backend.security.UserDetailsImpl; // Kullanıcıya özel sepet için
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/cart") // Gerçek erişim /api/cart olacak
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Frontend adresinize göre güncelleyin
@Tag(name = "Cart", description = "Shopping Cart APIs")
public class CartController {
 
    private final CartService cartService;
 
    @Operation(summary = "Add item to cart", description = "Adds a product to the shopping cart or updates its quantity if it already exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added/updated successfully, returns the updated cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class}))),
            @ApiResponse(responseCode = "400", description = "Invalid product ID or quantity, or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping
    public ResponseEntity<CartDTO> addToCart(
            @Parameter(description = "DTO containing productId and quantity", required = true)
            @RequestBody AddToCartRequestDTO addToCartRequest) {
        return ResponseEntity.ok(cartService.addItemToCart(null, null, addToCartRequest.getProductId(), addToCartRequest.getQuantity()));
    }
 
    @Operation(summary = "View cart", description = "Retrieves all items currently in the shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cart items",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class})))
    })
    @GetMapping
    public ResponseEntity<CartDTO> viewCart() {
        return ResponseEntity.ok(cartService.getCartDTO(null, null));
    }
 
    @Operation(summary = "Update item quantity in cart", description = "Updates the quantity of a specific item in the cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item quantity updated successfully, returns the updated cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class}))),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock"),
            @ApiResponse(responseCode = "404", description = "Product not found in cart")
    })
    @PutMapping("/{productId}")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @Parameter(description = "ID of the product to update", required = true) @PathVariable Long productId,
            @Parameter(description = "Payload containing the new quantity", required = true) @RequestBody UpdateQuantityRequestDTO quantityRequest) {
        if (quantityRequest.getQuantity() == null || quantityRequest.getQuantity() < 0) {
            throw new BadRequestException("Quantity must be a non-negative integer.");
        }
        return ResponseEntity.ok(cartService.updateItemQuantity(null, null, productId, quantityRequest.getQuantity()));
    }
 
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully, returns the updated cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class}))),
            @ApiResponse(responseCode = "404", description = "Product not found in cart")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(
            @Parameter(description = "ID of the product to remove", required = true) @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(null, null, productId));
    }
 
    @Operation(summary = "Clear cart", description = "Removes all items from the shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully, returns an empty cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class})))
    })
    @DeleteMapping
    public ResponseEntity<CartDTO> clearCart() {
        return ResponseEntity.ok(cartService.clearCart(null, null));
    }
 
    // Checkout işlemi genellikle ayrı bir OrderController'a taşınır.
    // Mevcut CartController'ınızda bir checkout metodu vardı, ancak bu sipariş oluşturma mantığını içermelidir.
    // Bu endpoint burada kalacaksa, OrderService'i çağırıp sipariş oluşturmalı ve sepeti temizlemelidir.
    // Şimdilik bu endpoint'i yorum satırına alıyorum, çünkü CheckoutComponent'unuz OrderService'i kullanıyor.
    /*
    @Operation(summary = "Checkout cart", description = "Converts the cart into an order and clears the cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout successful, order created"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or other validation error"),
            @ApiResponse(responseCode = "401", description = "User not authenticated (if required for checkout)")
    })
    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()") // Genellikle checkout için kullanıcı girişi gerekir
    public ResponseEntity<String> checkout(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        // Long userId = currentUser.getId();
        // Order createdOrder = orderService.createOrderFromCart(userId); // OrderService'te böyle bir metod olmalı
        // cartService.clearCart(userId);
        // return ResponseEntity.ok("Checkout successful! Order ID: " + createdOrder.getId());
        return ResponseEntity.ok(cartService.checkout()); // Eski basit hali
    }
    */
}
 
// Controller'a gönderilecek istek body'leri için basit DTO'lar
class AddToCartRequestDTO {
    private Long productId;
    private Integer quantity;
 
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
 
class UpdateQuantityRequestDTO {
    private Integer quantity;
 
    // Getters and Setters
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}