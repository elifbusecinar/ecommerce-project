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
import org.springframework.web.bind.annotation.*;
 
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
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
            @RequestBody AddToCartRequestDTO addToCartRequest,
            HttpSession session) {
        String guestCartId = session.getId();
        return ResponseEntity.ok(cartService.addItemToCart(null, guestCartId, addToCartRequest.getProductId(), addToCartRequest.getQuantity()));
    }
 
    @Operation(summary = "View cart", description = "Retrieves all items currently in the shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cart items",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class})))
    })
    @GetMapping
    public ResponseEntity<CartDTO> viewCart(HttpSession session) {
        String guestCartId = session.getId();
        return ResponseEntity.ok(cartService.getCartDTO(null, guestCartId));
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
            @Parameter(description = "Payload containing the new quantity", required = true) @RequestBody UpdateQuantityRequestDTO quantityRequest,
            HttpSession session) {
        if (quantityRequest.getQuantity() == null || quantityRequest.getQuantity() < 0) {
            throw new BadRequestException("Quantity must be a non-negative integer.");
        }
        String guestCartId = session.getId();
        return ResponseEntity.ok(cartService.updateItemQuantity(null, guestCartId, productId, quantityRequest.getQuantity()));
    }
 
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully, returns the updated cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class}))),
            @ApiResponse(responseCode = "404", description = "Product not found in cart")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartDTO> removeFromCart(
            @Parameter(description = "ID of the product to remove", required = true) @PathVariable Long productId,
            HttpSession session) {
        String guestCartId = session.getId();
        return ResponseEntity.ok(cartService.removeItemFromCart(null, guestCartId, productId));
    }
 
    @Operation(summary = "Clear cart", description = "Removes all items from the shopping cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully, returns an empty cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class, subTypes = {OrderItemDTO.class})))
    })
    @DeleteMapping
    public ResponseEntity<CartDTO> clearCart(HttpSession session) {
        String guestCartId = session.getId();
        return ResponseEntity.ok(cartService.clearCart(null, guestCartId));
    }
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