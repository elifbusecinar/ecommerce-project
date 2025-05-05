package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Get all products",
        description = "Retrieves a paginated list of all products"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized")
    })
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a specific product by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
        @Parameter(description = "ID of the product to retrieve") @PathVariable Long id
    ) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
        summary = "Search products",
        description = "Search products by query string with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
        @Parameter(description = "Search query string") @RequestParam String query,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @Operation(
        summary = "Get products by category",
        description = "Retrieves products filtered by category ID with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(
        @Parameter(description = "ID of the category to filter by") @PathVariable Long categoryId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @Operation(
        summary = "Get similar products",
        description = "Retrieves products similar to the specified product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved similar products"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<Product>> getSimilarProducts(
        @Parameter(description = "ID of the product to find similar items for") @PathVariable Long id,
        @Parameter(description = "Category ID for filtering similar products") @RequestParam Long categoryId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getSimilarProducts(id, categoryId, pageable));
    }

    @Operation(
        summary = "Get top rated products",
        description = "Retrieves products with ratings above the specified minimum"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved top rated products"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @GetMapping("/top-rated")
    public ResponseEntity<Page<Product>> getTopRatedProducts(
        @Parameter(description = "Minimum rating threshold (default: 4.0)") 
        @RequestParam(defaultValue = "4.0") double minRating,
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getTopRatedProducts(minRating, pageable));
    }

    @Operation(
        summary = "Create new product",
        description = "Creates a new product (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created product"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(
        @Parameter(description = "Product object to create") @RequestBody Product product
    ) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @Operation(
        summary = "Update product",
        description = "Updates an existing product (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
        @Parameter(description = "ID of the product to update") @PathVariable Long id,
        @Parameter(description = "Updated product details") @RequestBody Product productDetails
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    @Operation(
        summary = "Delete product",
        description = "Deletes a product (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "ID of the product to delete") @PathVariable Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Update product stock",
        description = "Updates the stock quantity of a product (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated stock"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStock(
        @Parameter(description = "ID of the product to update stock for") @PathVariable Long id,
        @Parameter(description = "New stock quantity") @RequestParam int quantity
    ) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Get low stock products",
        description = "Retrieves products with low stock levels (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved low stock products"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        return ResponseEntity.ok(productService.getLowStockProducts());
    }
}

